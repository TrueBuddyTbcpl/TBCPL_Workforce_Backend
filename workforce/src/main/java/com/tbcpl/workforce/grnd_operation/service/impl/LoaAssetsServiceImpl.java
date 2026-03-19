package com.tbcpl.workforce.grnd_operation.service.impl;


import com.tbcpl.workforce.common.util.S3Service;
import com.tbcpl.workforce.grnd_operation.dto.response.LoaAssetsResponseDto;
import com.tbcpl.workforce.grnd_operation.entity.LoaAssets;
import com.tbcpl.workforce.grnd_operation.repository.LoaAssetsRepository;
import com.tbcpl.workforce.grnd_operation.service.LoaAssetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoaAssetsServiceImpl implements LoaAssetsService {

    private final LoaAssetsRepository loaAssetsRepository;
    private final S3Service s3Service;

    private static final String FOLDER = "loa-assets";

    // ─── Get ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public LoaAssetsResponseDto getAssets() {
        return toDto(loaAssetsRepository.findTopByOrderByIdAsc().orElse(null));
    }

    // ─── Uploads ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LoaAssetsResponseDto uploadLogo(MultipartFile file) {
        LoaAssets assets = getOrCreate();
        safeDelete(assets.getLogoPublicId());
        Map<String, String> result = upload(file);
        assets.setLogoUrl(result.get("url"));
        assets.setLogoPublicId(result.get("key"));
        log.info("LOA logo uploaded: {}", result.get("key"));
        return toDto(loaAssetsRepository.save(assets));
    }

    @Override
    @Transactional
    public LoaAssetsResponseDto uploadStamp(MultipartFile file) {
        LoaAssets assets = getOrCreate();
        safeDelete(assets.getStampPublicId());
        Map<String, String> result = upload(file);
        assets.setStampUrl(result.get("url"));
        assets.setStampPublicId(result.get("key"));
        log.info("LOA stamp uploaded: {}", result.get("key"));
        return toDto(loaAssetsRepository.save(assets));
    }

    @Override
    @Transactional
    public LoaAssetsResponseDto uploadSignature(MultipartFile file) {
        LoaAssets assets = getOrCreate();
        safeDelete(assets.getSignaturePublicId());
        Map<String, String> result = upload(file);
        assets.setSignatureUrl(result.get("url"));
        assets.setSignaturePublicId(result.get("key"));
        log.info("LOA signature uploaded: {}", result.get("key"));
        return toDto(loaAssetsRepository.save(assets));
    }

    // ─── Deletes ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LoaAssetsResponseDto deleteLogo() {
        LoaAssets assets = getOrCreate();
        safeDelete(assets.getLogoPublicId());
        assets.setLogoUrl(null);
        assets.setLogoPublicId(null);
        return toDto(loaAssetsRepository.save(assets));
    }

    @Override
    @Transactional
    public LoaAssetsResponseDto deleteStamp() {
        LoaAssets assets = getOrCreate();
        safeDelete(assets.getStampPublicId());
        assets.setStampUrl(null);
        assets.setStampPublicId(null);
        return toDto(loaAssetsRepository.save(assets));
    }

    @Override
    @Transactional
    public LoaAssetsResponseDto deleteSignature() {
        LoaAssets assets = getOrCreate();
        safeDelete(assets.getSignaturePublicId());
        assets.setSignatureUrl(null);
        assets.setSignaturePublicId(null);
        return toDto(loaAssetsRepository.save(assets));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private LoaAssets getOrCreate() {
        return loaAssetsRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> loaAssetsRepository.save(LoaAssets.builder().build()));
    }

    private Map<String, String> upload(MultipartFile file) {
        try {
            return s3Service.uploadFile(file, FOLDER);
        } catch (Exception e) {
            log.error("Cloudinary upload failed for LOA asset", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "File upload failed: " + e.getMessage());
        }
    }

    private void safeDelete(String publicId) {
        if (publicId != null && !publicId.isBlank()) {
            try {
                s3Service.deleteFile(publicId);
            } catch (Exception e) {
                log.warn("Failed to delete old LOA asset from Cloudinary: {}", publicId);
            }
        }
    }

    private LoaAssetsResponseDto toDto(LoaAssets assets) {
        if (assets == null) return LoaAssetsResponseDto.builder().build();
        return LoaAssetsResponseDto.builder()
                .id(assets.getId())
                .logoUrl(assets.getLogoUrl())
                .stampUrl(assets.getStampUrl())
                .signatureUrl(assets.getSignatureUrl())
                .build();
    }
}
