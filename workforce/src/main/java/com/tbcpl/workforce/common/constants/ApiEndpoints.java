package com.tbcpl.workforce.common.constants;

/**
 * Centralized API endpoint constants for the entire application.
 * All controllers must reference these constants — no hardcoded strings.
 */
public final class ApiEndpoints {

    private ApiEndpoints() {}

    // ─── API version prefix ───────────────────────────────────────────────────
    public static final String API_V1 = "/api/v1";

    // ─── Department-based base paths ──────────────────────────────────────────
    public static final String AUTH_BASE      = API_V1 + "/auth";
    public static final String ADMIN_BASE     = API_V1 + "/admin";
    public static final String HR_BASE        = API_V1 + "/hr";
    public static final String OPERATION_BASE = API_V1 + "/operation";
    public static final String ACCOUNTS_BASE  = API_V1 + "/accounts";
    // ─── Employee Meta endpoints (dropdown support) ───────────────────────────
    public static final String META_DEPARTMENTS = "/meta/departments";
    public static final String META_ROLES       = "/meta/roles";

    // ─── Auth endpoints ───────────────────────────────────────────────────────
    public static final String AUTH_LOGIN           = "/login";
    public static final String AUTH_LOGOUT          = "/logout";
    public static final String AUTH_CHANGE_PASSWORD = "/change-password";
    public static final String AUTH_RESET_PASSWORD  = "/reset-password";
    public static final String AUTH_PROFILE         = "/profile";
    public static final String AUTH_VERIFY_EMAIL    = "/verify-email";       // ← NEW
    public static final String AUTH_RESEND_VERIFY   = "/resend-verification/{empId}"; // ← NEW

    // ─── Role endpoints ───────────────────────────────────────────────────────
    public static final String ROLES            = "/roles";
    public static final String ROLE_BY_ID       = "/roles/{id}";
    public static final String ROLES_ASSIGNABLE = "/roles/assignable";       // ← NEW

    // ─── Department endpoints ─────────────────────────────────────────────────
    public static final String DEPARTMENTS      = "/departments";
    public static final String DEPARTMENT_BY_ID = "/departments/{id}";

    // ─── Employee endpoints ───────────────────────────────────────────────────
    public static final String EMPLOYEES              = "/employees";
    public static final String EMPLOYEE_BY_ID         = "/employees/{id}";
    public static final String EMPLOYEE_BY_EMP_ID     = "/employees/emp/{empId}";
    public static final String EMPLOYEE_PROFILE_PHOTO = "/employees/{id}/profile-photo"; // ← NEW
    public static final String EMPLOYEE_REPORTING_MGRS = "/employees/reporting-managers"; // ← NEW

    // ─── Login Attempt Log endpoints ──────────────────────────────────────────
    public static final String LOGIN_ATTEMPTS         = "/login-attempts";
    public static final String LOGIN_ATTEMPT_BY_ID    = "/login-attempts/{id}";
    public static final String LOGIN_ATTEMPTS_BLOCKED    = "/login-attempts/blocked";
    public static final String LOGIN_ATTEMPTS_BY_EMPLOYEE = "/login-attempts/employee/{empId}";
    public static final String EMPLOYEE_UPDATE    = "/employees/{id}";

    public static final String PASSWORD_BASE          = "/password";
    public static final String PASSWORD_CHANGE        = "/password/change";
    public static final String PASSWORD_RESET_REQUEST = "/password/reset-request";
    public static final String PASSWORD_RESET_CONFIRM = "/password/reset-confirm";
    public static final String PASSWORD_ADMIN_RESET   = "/password/admin-reset";

    // ─── Proposal endpoints ───────────────────────────────────────────────────────
    public static final String PROPOSALS                    = "/proposals";
    public static final String PROPOSAL_BY_ID               = "/proposals/{id}";
    public static final String PROPOSAL_BACKGROUND          = "/proposals/{id}/background";
    public static final String PROPOSAL_SCOPE               = "/proposals/{id}/scope";
    public static final String PROPOSAL_METHODOLOGY         = "/proposals/{id}/methodology";
    public static final String PROPOSAL_FEE                 = "/proposals/{id}/fee";
    public static final String PROPOSAL_PAYMENT_TERMS       = "/proposals/{id}/payment-terms";
    public static final String PROPOSAL_CONFIDENTIALITY     = "/proposals/{id}/confidentiality";
    public static final String PROPOSAL_OBLIGATIONS         = "/proposals/{id}/obligations";
    public static final String PROPOSAL_CONCLUSION          = "/proposals/{id}/conclusion";
    public static final String PROPOSAL_STEPS               = "/proposals/{id}/steps";
    public static final String PROPOSAL_STATUS              = "/proposals/{id}/status";
    public static final String PROPOSAL_SIGNATURE           = "/proposals/{id}/signature";
    public static final String PROPOSAL_PDF                 = "/proposals/{id}/pdf";
    public static final String PROPOSAL_SEND_EMAIL          = "/proposals/{id}/send-email";

    // Proposal — Step 2 (Section Builder)
    public static final String PROPOSAL_SECTIONS          = "/proposals/{id}/sections";
    public static final String PROPOSAL_SECTION_BY_ID     = "/proposals/{id}/sections/{sectionId}";
    public static final String PROPOSAL_SECTIONS_REORDER  = "/proposals/{id}/sections/reorder";

    // ─── HR Module endpoints ──────────────────────────────────────────────────

    // Employee Profile
    public static final String HR_EMPLOYEE_PROFILES         = "/employee-profiles";
    public static final String HR_EMPLOYEE_PROFILE_BY_ID    = "/employee-profiles/{id}";
    public static final String HR_EMPLOYEE_PROFILE_BY_EMP   = "/employee-profiles/emp/{empId}";

    // Attendance
    public static final String HR_ATTENDANCE                = "/attendance";
    public static final String HR_ATTENDANCE_BY_ID          = "/attendance/{id}";
    public static final String HR_ATTENDANCE_BY_EMP         = "/attendance/emp/{empId}";
    public static final String HR_ATTENDANCE_MONTHLY        = "/attendance/emp/{empId}/monthly";

    // Leave Types
    public static final String HR_LEAVE_TYPES               = "/leave-types";
    public static final String HR_LEAVE_TYPE_BY_ID          = "/leave-types/{id}";

    public static final String HR_EMPLOYEES = "/employees";

    // Holidays
    public static final String HR_HOLIDAYS                  = "/holidays";
    public static final String HR_HOLIDAY_BY_ID             = "/holidays/{id}";

    // Leave Applications
    public static final String HR_LEAVE_APPLICATIONS        = "/leave-applications";
    public static final String HR_LEAVE_APPLICATION_BY_ID   = "/leave-applications/{id}";
    public static final String HR_LEAVE_APPLICATION_ACTION  = "/leave-applications/{id}/action";
    public static final String HR_LEAVE_BALANCE_BY_EMP      = "/leave-balance/emp/{empId}";

    // Salary Structure
    public static final String HR_SALARY_STRUCTURES         = "/salary-structures";
    public static final String HR_SALARY_STRUCTURE_BY_ID    = "/salary-structures/{id}";
    public static final String HR_SALARY_STRUCTURE_BY_EMP   = "/salary-structures/emp/{empId}";

    // Payroll Inputs
    public static final String HR_PAYROLL_INPUTS            = "/payroll-inputs";
    public static final String HR_PAYROLL_INPUT_BY_ID       = "/payroll-inputs/{id}";
    public static final String HR_PAYROLL_INPUTS_BY_EMP     = "/payroll-inputs/emp/{empId}";
    public static final String HR_SALARY_STRUCTURE_CURRENT = "/salary-structures/emp/{empId}/current";
    public static final String HR_SALARY_STRUCTURE_HISTORY = "/salary-structures/emp/{empId}/history";
    public static final String HR_PAYROLL_INPUTS_SUBMIT    = "/payroll-inputs/submit";

    // Recruitment
    public static final String HR_JOB_REQUISITIONS          = "/job-requisitions";
    public static final String HR_JOB_REQUISITION_BY_ID     = "/job-requisitions/{id}";
    public static final String HR_CANDIDATES                = "/candidates";
    public static final String HR_CANDIDATE_BY_ID           = "/candidates/{id}";
    public static final String HR_OFFER_LETTERS             = "/offer-letters";
    public static final String HR_OFFER_LETTER_BY_ID        = "/offer-letters/{id}";
    public static final String HR_INTERVIEWS             = "/interviews";
    public static final String HR_INTERVIEW_BY_ID        = "/interviews/{id}";

    // Performance
    public static final String HR_APPRAISAL_CYCLES          = "/appraisal-cycles";
    public static final String HR_APPRAISAL_CYCLE_BY_ID     = "/appraisal-cycles/{id}";
    public static final String HR_KRAS                      = "/kras";
    public static final String HR_KRA_BY_ID                 = "/kras/{id}";
    public static final String HR_PERFORMANCE_REVIEWS       = "/performance-reviews";
    public static final String HR_PERFORMANCE_REVIEW_BY_ID  = "/performance-reviews/{id}";
    public static final String HR_KRA_TEMPLATES         = "/kra-templates";
    public static final String HR_KRA_TEMPLATE_BY_ID    = "/kra-templates/{id}";
    public static final String HR_APPRAISALS            = "/appraisals";
    public static final String HR_APPRAISAL_BY_ID       = "/appraisals/{id}";

    // Documents
    public static final String HR_DOCUMENTS                 = "/documents";
    public static final String HR_DOCUMENT_BY_ID            = "/documents/{id}";
    public static final String HR_DOCUMENTS_BY_EMP          = "/documents/emp/{empId}";
    public static final String HR_LETTERS         = "/letters";
    public static final String HR_LETTER_BY_ID    = "/letters/{id}";

    // Grievance
    public static final String HR_GRIEVANCES                = "/grievances";
    public static final String HR_GRIEVANCE_BY_ID           = "/grievances/{id}";
    public static final String HR_GRIEVANCE_ACTION          = "/grievances/{id}/action";
    public static final String HR_DISCIPLINARY_ACTIONS       = "/disciplinary-actions";
    public static final String HR_DISCIPLINARY_ACTION_BY_ID  = "/disciplinary-actions/{id}";

    public static final String CLIENTS = "/clients";
    public static final String CLIENT_BY_ID = "/clients/{id}";
    public static final String CLIENT_LOGO = "/clients/{id}/logo";
    public static final String ADMIN_DROPDOWN_CLIENTS = "/dropdowns/clients";
    // In ApiEndpoints.java, add under OPERATION_BASE section:
    public static final String OPERATION_DROPDOWN_CLIENTS = "/dropdowns/clients";
    // In ApiEndpoints.java

    // ─── Common/Shared endpoints (accessible by all departments) ─────────────
    public static final String COMMON_BASE = API_V1 + "/common";
    public static final String COMMON_DROPDOWN_CLIENTS = "/dropdowns/clients";



}
