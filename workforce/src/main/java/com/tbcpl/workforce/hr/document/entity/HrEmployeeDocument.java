package com.tbcpl.workforce.hr.document.entity;

import com.tbcpl.workforce.hr.document.entity.enums.DocumentStatus;
import com.tbcpl.workforce.hr.document.entity.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_employee_documents",
        indexes = {
                @Index(name = "idx_emp_doc_emp_id",       columnList = "emp_id"),
                @Index(name = "idx_emp_doc_type",         columnList = "document_type"),
                @Index(name = "idx_emp_doc_status",       columnList = "status"),
                @Index(name = "idx_emp_doc_is_active",    columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrEmployeeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-dept reference — no JPA join
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    // Friendly name e.g. "Aadhaar Card", "PAN Card"
    @Column(name = "document_name", nullable = false, length = 150)
    private String documentName;

    // Stored file path/URL (S3, local, etc.)
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    // Original file name for display
    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    // MIME type e.g. application/pdf, image/jpeg
    @Column(name = "file_mime_type", length = 100)
    private String fileMimeType;

    // File size in KB
    @Column(name = "file_size_kb")
    private Long fileSizeKb;

    // Document number e.g. Aadhaar number, PAN number
    @Column(name = "document_number", length = 50)
    private String documentNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.PENDING_VERIFICATION;

    // HR notes during verification
    @Column(name = "verification_remarks", length = 500)
    private String verificationRemarks;

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "is_mandatory", nullable = false)
    @Builder.Default
    private Boolean isMandatory = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}