// com.tbcpl.workforce.common.email.template.ReportEmailTemplate.java
package com.tbcpl.workforce.common.email.template;

import com.tbcpl.workforce.common.dto.ReportEmailRequestDto;

public interface ReportEmailTemplate {

    /**
     * Returns the email subject line for this report type.
     */
    String buildSubject(ReportEmailRequestDto request);

    /**
     * Returns the full HTML body for this report type.
     */
    String buildHtmlBody(ReportEmailRequestDto request);
}