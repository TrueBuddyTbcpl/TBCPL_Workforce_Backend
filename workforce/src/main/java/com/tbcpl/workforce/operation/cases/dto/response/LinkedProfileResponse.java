package com.tbcpl.workforce.operation.cases.dto.response;

import java.time.LocalDateTime;

public record LinkedProfileResponse(
        Long id,
        Long profileId,
        String profileNumber,
        String profileName,
        String linkedBy,
        LocalDateTime linkedAt
) {}