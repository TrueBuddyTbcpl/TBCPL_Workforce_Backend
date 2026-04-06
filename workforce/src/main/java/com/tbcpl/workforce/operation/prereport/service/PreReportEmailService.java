package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.operation.prereport.dto.request.PreReportSendMailRequestDto;

public interface PreReportEmailService {
    void sendPreReportMail(String reportId, PreReportSendMailRequestDto request);
}