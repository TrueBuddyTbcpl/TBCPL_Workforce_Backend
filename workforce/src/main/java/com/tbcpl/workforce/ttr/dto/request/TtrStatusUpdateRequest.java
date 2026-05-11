package com.tbcpl.workforce.ttr.dto.request;

import com.tbcpl.workforce.ttr.entity.enums.TtrStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private TtrStatus newStatus;

    private String comments;

    private String proofFileUrl;
    private String proofFileName;
}