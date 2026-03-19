package com.tbcpl.workforce.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client    s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /**
     * Upload a MultipartFile to S3 under the given folder path.
     * Returns: url, key, fileName
     */
    public Map<String, String> uploadFile(MultipartFile file, String folder) throws IOException {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String key       = folder + "/" + UUID.randomUUID() + extension;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        String url = buildUrl(key);
        log.info("File uploaded to S3: {}", key);

        return Map.of(
                "url",       url,
                "key",       key,
                "file_name", file.getOriginalFilename() != null
                        ? file.getOriginalFilename() : key
        );
    }

    /**
     * Download file bytes directly from S3 by key.
     * Use this instead of public URL when bucket is private.
     */
    public byte[] downloadFileBytes(String key) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            return s3Client.getObjectAsBytes(getRequest).asByteArray();
        } catch (Exception e) {
            log.error("Failed to download file from S3 key {}: {}", key, e.getMessage());
            throw new RuntimeException("Failed to fetch file from S3: " + e.getMessage());
        }
    }


    /**
     * Upload raw bytes directly (used for cropped canvas images from frontend).
     */
    public Map<String, String> uploadBytes(byte[] bytes, String folder,
                                           String fileName, String contentType) {
        String extension = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.'))
                : ".jpg";
        String key = folder + "/" + UUID.randomUUID() + extension;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));

        String url = buildUrl(key);
        log.info("Bytes uploaded to S3: {}", key);

        return Map.of(
                "url",       url,
                "key",       key,
                "file_name", fileName
        );
    }

    /**
     * Delete a file from S3 by its key.
     */
    public void deleteFile(String key) {
        if (key == null || key.isBlank()) return;

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());

        log.info("File deleted from S3: {}", key);
    }

    /**
     * Generate a pre-signed URL valid for the given duration.
     * Use this for private files that should not be publicly accessible.
     */
    public String generatePresignedUrl(String key, Duration expiry) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build())
                .build();

        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String buildUrl(String key) {
        return String.format("https://%s/%s", cloudFrontDomain, key);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return ".bin";
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 20MB limit");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "File type not allowed. Allowed: JPG, PNG, WEBP, PDF, DOC, DOCX"
            );
        }
    }
}
