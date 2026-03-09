package com.tbcpl.workforce.operation.profile.service;

import com.tbcpl.workforce.operation.profile.dto.request.*;
import com.tbcpl.workforce.operation.profile.dto.response.*;
import com.tbcpl.workforce.operation.profile.enums.ProfileStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface OpProfileService {

    // Step 1 — Init profile
    ProfileDetailResponse initProfile(ProfileInitRequest request, String empId);

    // Steps 2–16
    ProfileDetailResponse savePhysicalAttributes(Long profileId, PhysicalAttributesRequest request, String empId);
    ProfileDetailResponse saveAddress(Long profileId, AddressRequest request, String empId);
    ProfileDetailResponse saveContactInfo(Long profileId, ContactInfoRequest request, String empId);
    ProfileDetailResponse saveIdentificationDocs(Long profileId, IdentificationDocsRequest request, String empId);
    ProfileDetailResponse saveBusinessActivities(Long profileId, BusinessActivitiesRequest request, String empId);
    ProfileDetailResponse saveEntityOrganization(Long profileId, EntityOrganizationRequest request, String empId);
    ProfileDetailResponse saveGeographicExposure(Long profileId, GeographicExposureRequest request, String empId);
    ProfileDetailResponse saveRelatedFIRs(Long profileId, RelatedFIRsRequest request, String empId);
    ProfileDetailResponse saveMaterialSeized(Long profileId, MaterialSeizedRequest request, String empId);
    ProfileDetailResponse saveAssets(Long profileId, AssetsRequest request, String empId);
    ProfileDetailResponse saveKnownAssociates(Long profileId, KnownAssociatesRequest request, String empId);
    ProfileDetailResponse saveKnownEmployees(Long profileId, KnownEmployeesRequest request, String empId);
    ProfileDetailResponse saveProductsOperations(Long profileId, ProductsOperationsRequest request, String empId);
    ProfileDetailResponse saveFamilyBackground(Long profileId, FamilyBackgroundRequest request, String empId);
    ProfileDetailResponse saveInfluentialLinks(Long profileId, InfluentialLinksRequest request, String empId);
    ProfileDetailResponse saveCurrentStatus(Long profileId, CurrentStatusRequest request, String empId);
    ProfileDetailResponse saveAdditionalInfo(Long profileId, AdditionalInfoRequest request, String empId);

    // Read
    PagedProfileResponse getAllProfiles(Pageable pageable);
    PagedProfileResponse searchProfiles(String search, Pageable pageable);
    PagedProfileResponse getProfilesByStatus(ProfileStatus status, Pageable pageable);
    ProfileDetailResponse getProfileById(Long profileId);

    // Step status
    java.util.List<StepStatusResponse> getStepStatuses(Long profileId);

    // Change log
    ChangeLogPagedResponse getChangeLog(Long profileId, Pageable pageable);

    // Soft delete
    void deleteProfile(Long profileId, String empId);

    // Image upload
    ImageUploadResponse uploadImage(MultipartFile file, String folder);
}
