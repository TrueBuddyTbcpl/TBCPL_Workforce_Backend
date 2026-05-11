package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalListItemResponse {

    private Long           id;
    private String         proposalCode;
    private Long           clientId;
    private ProposalStatus status;
    private String         projectTitle;
    private String         createdBy;
    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
}