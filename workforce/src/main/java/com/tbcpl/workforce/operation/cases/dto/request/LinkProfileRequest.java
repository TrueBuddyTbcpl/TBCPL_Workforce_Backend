// ── Request ────────────────────────────────────────────────────────────────
// com/tbcpl/workforce/operation/cases/dto/request/LinkProfileRequest.java
package com.tbcpl.workforce.operation.cases.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record LinkProfileRequest(
        @NotNull Long profileId,
        @NotNull String profileNumber,
        @NotNull String profileName
) {}


