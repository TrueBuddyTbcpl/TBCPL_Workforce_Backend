package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.StepName;
import com.tbcpl.workforce.admin.proposal.entity.enums.StepStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalStepStatusResponse {
    private StepName stepName;
    private StepStatus status;
}
