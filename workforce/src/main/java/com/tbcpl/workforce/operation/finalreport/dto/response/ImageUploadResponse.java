package com.tbcpl.workforce.operation.finalreport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {

    private List<UploadedImage> images;
    private int successCount;
    private int failedCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadedImage {
        private int index;
        private String originalName;
        private String url;
        private String publicId;
        private boolean success;
        private String error;
    }
}
