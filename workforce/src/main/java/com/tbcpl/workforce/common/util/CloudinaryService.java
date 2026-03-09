package com.tbcpl.workforce.common.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final java.util.Set<String> ALLOWED_TYPES = java.util.Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @SuppressWarnings("unchecked")
    public Map<String, String> uploadFile(MultipartFile file, String folder) throws IOException {
        validateFile(file);

        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder",          "tbcpl-workforce/" + folder,
                        "resource_type",   "auto",
                        "use_filename",    true,
                        "unique_filename", true
                )
        );

        log.info("File uploaded to Cloudinary: {}", uploadResult.get("public_id"));

        return Map.of(
                "url",       (String) uploadResult.get("secure_url"),
                "public_id", (String) uploadResult.get("public_id"),
                "file_name", (String) uploadResult.get("original_filename")
        );
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "auto"));
        log.info("File deleted from Cloudinary: {}", publicId);
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
