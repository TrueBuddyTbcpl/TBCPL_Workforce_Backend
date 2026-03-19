package com.tbcpl.workforce.grnd_operation.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoaAssetsResponseDto {
    private Long   id;
    private String logoUrl;
    private String stampUrl;
    private String signatureUrl;
}
