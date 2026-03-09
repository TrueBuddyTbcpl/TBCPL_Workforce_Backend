package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageUploadRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;

    private String folder; // optional: cloudinary subfolder e.g. "profiles/aadhaar"
}
