// package com.tbcpl.workforce.operation.customoption.dto.request;
package com.tbcpl.workforce.operation.customoption.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomOptionRequest {

    @NotBlank(message = "Field name must not be blank")
    @Size(max = 100, message = "Field name must not exceed 100 characters")
    private String fieldName;

    @NotBlank(message = "Value must not be blank")
    @Size(max = 200, message = "Value must not exceed 200 characters")
    private String value;

    private String createdBy;
}