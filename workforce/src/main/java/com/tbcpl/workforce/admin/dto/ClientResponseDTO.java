package com.tbcpl.workforce.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {
    private Long clientId;
    private String clientName;
    private String logoFileName;
    private boolean hasLogo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
