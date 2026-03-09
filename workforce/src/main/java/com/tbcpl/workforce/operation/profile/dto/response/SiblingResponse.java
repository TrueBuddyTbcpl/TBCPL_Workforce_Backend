package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.SiblingRelationship;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiblingResponse {
    private Long id;
    private String name;
    private SiblingRelationship relationship;
    private String occupation;
}
