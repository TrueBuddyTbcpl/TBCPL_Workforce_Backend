package com.tbcpl.workforce.operation.profile.controller;

import com.tbcpl.workforce.operation.profile.dto.request.*;
import com.tbcpl.workforce.operation.profile.dto.response.*;
import com.tbcpl.workforce.operation.profile.enums.ProfileStatus;
import com.tbcpl.workforce.operation.profile.service.OpProfileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation/profiles")
@Slf4j
public class OpProfileController {

    private final OpProfileService profileService;

    public OpProfileController(OpProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "profiles/photos") String folder,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("POST /upload-image - empId: {}, folder: {}, size: {} bytes",
                empId, folder, file.getSize());
        ImageUploadResponse response = profileService.uploadImage(file, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/init")
    public ResponseEntity<ProfileDetailResponse> initProfile(
            @Valid @RequestBody ProfileInitRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("POST /init - empId: {}", empId);
        ProfileDetailResponse response = profileService.initProfile(request, empId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{profileId}/personal-info")
    public ResponseEntity<ProfileDetailResponse> savePersonalInfo(
            @PathVariable Long profileId,
            @Valid @RequestBody ProfileInitRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /profiles/{}/personal-info - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.savePersonalInfo(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/address")
    public ResponseEntity<ProfileDetailResponse> saveAddress(
            @PathVariable Long profileId,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/address - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveAddress(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/contact-info")
    public ResponseEntity<ProfileDetailResponse> saveContactInfo(
            @PathVariable Long profileId,
            @Valid @RequestBody ContactInfoRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/contact-info - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveContactInfo(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/identification-docs")
    public ResponseEntity<ProfileDetailResponse> saveIdentificationDocs(
            @PathVariable Long profileId,
            @Valid @RequestBody IdentificationDocsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/identification-docs - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveIdentificationDocs(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/business-activities")
    public ResponseEntity<ProfileDetailResponse> saveBusinessActivities(
            @PathVariable Long profileId,
            @Valid @RequestBody BusinessActivitiesRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/business-activities - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveBusinessActivities(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/entity-organization")
    public ResponseEntity<ProfileDetailResponse> saveEntityOrganization(
            @PathVariable Long profileId,
            @Valid @RequestBody EntityOrganizationRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/entity-organization - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveEntityOrganization(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/geographic-exposure")
    public ResponseEntity<ProfileDetailResponse> saveGeographicExposure(
            @PathVariable Long profileId,
            @Valid @RequestBody GeographicExposureRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/geographic-exposure - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveGeographicExposure(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/related-firs")
    public ResponseEntity<ProfileDetailResponse> saveRelatedFIRs(
            @PathVariable Long profileId,
            @Valid @RequestBody RelatedFIRsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/related-firs - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveRelatedFIRs(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/material-seized")
    public ResponseEntity<ProfileDetailResponse> saveMaterialSeized(
            @PathVariable Long profileId,
            @Valid @RequestBody MaterialSeizedRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/material-seized - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveMaterialSeized(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/assets")
    public ResponseEntity<ProfileDetailResponse> saveAssets(
            @PathVariable Long profileId,
            @Valid @RequestBody AssetsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/assets - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveAssets(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/known-associates")
    public ResponseEntity<ProfileDetailResponse> saveKnownAssociates(
            @PathVariable Long profileId,
            @Valid @RequestBody KnownAssociatesRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/known-associates - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveKnownAssociates(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/known-employees")
    public ResponseEntity<ProfileDetailResponse> saveKnownEmployees(
            @PathVariable Long profileId,
            @Valid @RequestBody KnownEmployeesRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/known-employees - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveKnownEmployees(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/products-operations")
    public ResponseEntity<ProfileDetailResponse> saveProductsOperations(
            @PathVariable Long profileId,
            @Valid @RequestBody ProductsOperationsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/products-operations - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveProductsOperations(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/family-background")
    public ResponseEntity<ProfileDetailResponse> saveFamilyBackground(
            @PathVariable Long profileId,
            @Valid @RequestBody FamilyBackgroundRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/family-background - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveFamilyBackground(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/influential-links")
    public ResponseEntity<ProfileDetailResponse> saveInfluentialLinks(
            @PathVariable Long profileId,
            @Valid @RequestBody InfluentialLinksRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/influential-links - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveInfluentialLinks(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/current-status")
    public ResponseEntity<ProfileDetailResponse> saveCurrentStatus(
            @PathVariable Long profileId,
            @Valid @RequestBody CurrentStatusRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/current-status - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveCurrentStatus(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/additional-info")
    public ResponseEntity<ProfileDetailResponse> saveAdditionalInfo(
            @PathVariable Long profileId,
            @Valid @RequestBody AdditionalInfoRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /{}/additional-info - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveAdditionalInfo(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ── READ-ONLY ENDPOINTS (no authentication parameter needed) ─────────────

    @GetMapping
    public ResponseEntity<PagedProfileResponse> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("GET /profiles - page: {}, size: {}", page, size);
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(profileService.getAllProfiles(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedProfileResponse> searchProfiles(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /profiles/search - query: {}", query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(profileService.searchProfiles(query, pageable));
    }

    @GetMapping("/by-status")
    public ResponseEntity<PagedProfileResponse> getByStatus(
            @RequestParam ProfileStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /profiles/by-status - status: {}", status);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(profileService.getProfilesByStatus(status, pageable));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDetailResponse> getProfileById(
            @PathVariable Long profileId) {

        log.info("GET /profiles/{}", profileId);
        return ResponseEntity.ok(profileService.getProfileById(profileId));
    }

    @GetMapping("/{profileId}/steps")
    public ResponseEntity<List<StepStatusResponse>> getStepStatuses(
            @PathVariable Long profileId) {

        log.info("GET /profiles/{}/steps", profileId);
        return ResponseEntity.ok(profileService.getStepStatuses(profileId));
    }

    @GetMapping("/{profileId}/change-log")
    public ResponseEntity<ChangeLogPagedResponse> getChangeLog(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /profiles/{}/change-log", profileId);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(profileService.getChangeLog(profileId, pageable));
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable Long profileId,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("DELETE /profiles/{} - empId: {}", profileId, empId);
        profileService.deleteProfile(profileId, empId);
        return ResponseEntity.noContent().build();
    }
}