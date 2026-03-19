package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.FirStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FirRequest {

    private String firNumber;

    private String caseNumber;
    private List<String> sections = new ArrayList<>();
    private String dateRegistered;
    private String status;
}
