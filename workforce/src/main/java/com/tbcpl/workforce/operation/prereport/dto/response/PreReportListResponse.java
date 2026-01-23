package com.tbcpl.workforce.operation.prereport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportListResponse {

    private List<PreReportResponse> reports;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
}
