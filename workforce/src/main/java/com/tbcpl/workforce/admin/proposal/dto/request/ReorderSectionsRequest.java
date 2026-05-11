package com.tbcpl.workforce.admin.proposal.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReorderSectionsRequest {

    /**
     * Ordered list of section IDs representing the new display order.
     * Index 0 = displayOrder 1, Index 1 = displayOrder 2, and so on.
     * Every existing section ID of this proposal must be present.
     */
    @NotEmpty(message = "sectionIds must not be empty")
    private List<Long> sectionIds;
}