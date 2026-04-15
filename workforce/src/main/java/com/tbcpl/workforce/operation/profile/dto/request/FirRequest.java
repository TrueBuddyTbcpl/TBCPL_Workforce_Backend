package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FirRequest {
    private String firNumber;
    private String caseNumber;
    private List<String> sections = new ArrayList<>();
    private String dateRegistered;

    // Already String — add companion field
    private String status;
    private String statusOther;
}