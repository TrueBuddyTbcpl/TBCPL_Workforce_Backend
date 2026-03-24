package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalServiceType;
import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProposalSummaryResponse {
    private Long proposalId;
    private String proposalCode;
    private Long clientId;
    private String clientName;
    private String suspectEntityName;
    private String projectTitle;
    private ProposalServiceType serviceType;
    private String serviceTypeDisplayName;
    private ProposalStatus status;
    private LocalDate proposalDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<ProposalStepStatusResponse> steps;
}
