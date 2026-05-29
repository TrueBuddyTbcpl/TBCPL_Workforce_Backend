package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import com.tbcpl.workforce.admin.proposal.entity.enums.ServiceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalResponse {

    private Long id;
    private String proposalCode;
    private Long clientId;
    private ServiceType serviceType;
    private String productName;
    private ProposalStatus status;
    private List<ProposalSectionResponse> sections;
    private String remarks;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}