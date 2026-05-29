package com.tbcpl.workforce.admin.proposal.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReorderSubSectionsRequest {

    /**
     * Ordered list of subsection IDs representing the new display order.
     * Index 0 = displayOrder 1, Index 1 = displayOrder 2, and so on.
     * Every existing subsection ID of this section must be present.
     */
    @NotEmpty(message = "subSectionIds must not be empty")
    private List<Long> subSectionIds;
}