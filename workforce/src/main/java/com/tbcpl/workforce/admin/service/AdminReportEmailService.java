// com.tbcpl.workforce.admin.service.AdminReportEmailService.java
package com.tbcpl.workforce.admin.service;

import com.tbcpl.workforce.admin.dto.request.SendReportEmailRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AdminReportEmailService {
    void sendReport(SendReportEmailRequest request, MultipartFile pdfFile);
}