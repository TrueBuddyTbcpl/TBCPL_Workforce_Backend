package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyBackgroundResponse {
    private String fatherName;
    private String fatherOccupation;
    private String fatherContact;
    private String motherName;
    private String motherOccupation;
    private String motherContact;
    private List<SiblingResponse> siblings;
}
