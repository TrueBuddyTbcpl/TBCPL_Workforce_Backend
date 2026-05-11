package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.SectionContentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSectionResponse {

    private Long               id;
    private String             sectionKey;
    private String             sectionTitle;
    private SectionContentType contentType;
    private String             content;
    private Integer            displayOrder;
    private boolean            visible;
    private String             createdBy;
    private LocalDateTime      createdAt;
    private LocalDateTime      updatedAt;
}