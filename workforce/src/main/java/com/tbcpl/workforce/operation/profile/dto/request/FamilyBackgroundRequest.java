package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FamilyBackgroundRequest {
    private String fatherName;
    private String fatherOccupation;
    private String fatherContact;
    private String motherName;
    private String motherOccupation;
    private String motherContact;

    @Valid
    private List<SiblingRequest> siblings = new ArrayList<>();
}
