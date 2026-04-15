package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmergencyContactRequest {

    @Size(max = 150, message = "Emergency contact name must not exceed 150 characters")
    private String name;

    @Size(max = 20, message = "Emergency contact phone must not exceed 20 characters")
    private String phone;

    @Size(max = 100, message = "Emergency contact relation must not exceed 100 characters")
    private String relation;
}