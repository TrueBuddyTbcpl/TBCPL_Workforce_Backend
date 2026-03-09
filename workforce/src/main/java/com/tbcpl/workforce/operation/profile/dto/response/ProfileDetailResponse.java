package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// Full profile — used in GET /profiles/{id}
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailResponse {
    private Long id;
    private String profileNumber;
    private String name;
    private ProfileStatus status;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // All steps
    private PersonalInfoResponse personalInfo;
    private PhysicalAttributesResponse physicalAttributes;
    private AddressResponse address;
    private ContactInfoResponse contactInfo;
    private IdentificationDocsResponse identificationDocs;
    private BusinessActivitiesResponse businessActivities;
    private GeographicExposureResponse geographicExposure;
    private List<AssociatedCompanyResponse> associatedCompanies;
    private List<FirResponse> firs;
    private List<MaterialSeizedItemResponse> materialSeized;
    private List<VehicleResponse> vehicles;
    private List<AssociateResponse> knownAssociates;
    private List<AssociateResponse> knownEmployees;
    private ProductsOperationsResponse productsOperations;
    private List<ProductInfringedResponse> productsInfringed;
    private FamilyBackgroundResponse familyBackground;
    private List<InfluentialLinkResponse> influentialLinks;
    private CurrentStatusResponse currentStatus;
    private AdditionalInfoResponse additionalInfo;

    // Step tracking
    private List<StepStatusResponse> stepStatuses;
}
