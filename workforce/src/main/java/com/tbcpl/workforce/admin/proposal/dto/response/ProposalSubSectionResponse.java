package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.SubSectionContentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSubSectionResponse {

    private Long id;
    private String subSectionKey;
    private String subSectionTitle;
    private SubSectionContentType contentType;
    private String content;
    private Integer displayOrder;
    private boolean visible;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}