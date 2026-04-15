package com.tbcpl.workforce.operation.customoption.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomOptionResponse {
    private Long id;
    private String fieldName;
    private String value;
}