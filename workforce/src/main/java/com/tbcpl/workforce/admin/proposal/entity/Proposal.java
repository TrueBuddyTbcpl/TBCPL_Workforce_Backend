package com.tbcpl.workforce.admin.proposal.entity;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalServiceType;
import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposal",
        indexes = {
                @Index(name = "idx_proposal_client_id",  columnList = "client_id"),
                @Index(name = "idx_proposal_code",        columnList = "proposal_code"),
                @Index(name = "idx_proposal_status",      columnList = "status"),
                @Index(name = "idx_proposal_deleted",     columnList = "is_deleted")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long proposalId;

    @Column(name = "proposal_code", unique = true, nullable = false, length = 20)
    private String proposalCode;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "client_company_type", length = 100)
    private String clientCompanyType;

    @Column(name = "suspect_entity_name", length = 255)
    private String suspectEntityName;

    @Column(name = "suspect_entity_type", length = 100)
    private String suspectEntityType;

    @Column(name = "project_title", length = 255)
    private String projectTitle;

    @Column(name = "proposal_date")
    private LocalDate proposalDate;

    @Column(name = "target_products", length = 500)
    private String targetProducts;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", length = 100)
    private ProposalServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ProposalStatus status;

    @Column(name = "prepared_by", length = 255)
    private String preparedBy;

    @Column(name = "signature_stamp_path", length = 500)
    private String signatureStampPath;

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
