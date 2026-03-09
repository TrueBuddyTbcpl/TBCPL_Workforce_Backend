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

    // ─────────────────────────────────────────────────────────────────────────
    // IMAGE UPLOAD
    // POST /api/v1/operation/profiles/upload-image
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder) {

        log.info("POST /api/v1/operation/profiles/upload-image - folder: {}", folder);
        ImageUploadResponse response = profileService.uploadImage(file, folder);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 1 — INIT PROFILE
    // POST /api/v1/operation/profiles/init
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/init")
    public ResponseEntity<ProfileDetailResponse> initProfile(
            @Valid @RequestBody ProfileInitRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("POST /api/v1/operation/profiles/init - empId: {}", empId);
        ProfileDetailResponse response = profileService.initProfile(request, empId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 2 — PHYSICAL ATTRIBUTES
    // PUT /api/v1/operation/profiles/{profileId}/physical-attributes
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/physical-attributes")
    public ResponseEntity<ProfileDetailResponse> savePhysicalAttributes(
            @PathVariable Long profileId,
            @Valid @RequestBody PhysicalAttributesRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/physical-attributes - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.savePhysicalAttributes(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 3 — ADDRESS
    // PUT /api/v1/operation/profiles/{profileId}/address
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/address")
    public ResponseEntity<ProfileDetailResponse> saveAddress(
            @PathVariable Long profileId,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/address - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveAddress(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 4 — CONTACT INFO
    // PUT /api/v1/operation/profiles/{profileId}/contact-info
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/contact-info")
    public ResponseEntity<ProfileDetailResponse> saveContactInfo(
            @PathVariable Long profileId,
            @Valid @RequestBody ContactInfoRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/contact-info - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveContactInfo(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 5 — IDENTIFICATION DOCS
    // PUT /api/v1/operation/profiles/{profileId}/identification-docs
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/identification-docs")
    public ResponseEntity<ProfileDetailResponse> saveIdentificationDocs(
            @PathVariable Long profileId,
            @Valid @RequestBody IdentificationDocsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/identification-docs - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveIdentificationDocs(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 6 — BUSINESS ACTIVITIES
    // PUT /api/v1/operation/profiles/{profileId}/business-activities
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/business-activities")
    public ResponseEntity<ProfileDetailResponse> saveBusinessActivities(
            @PathVariable Long profileId,
            @Valid @RequestBody BusinessActivitiesRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/business-activities - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveBusinessActivities(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 7 — ENTITY & ORGANIZATION
    // PUT /api/v1/operation/profiles/{profileId}/entity-organization
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/entity-organization")
    public ResponseEntity<ProfileDetailResponse> saveEntityOrganization(
            @PathVariable Long profileId,
            @Valid @RequestBody EntityOrganizationRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/entity-organization - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveEntityOrganization(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 8 — GEOGRAPHIC EXPOSURE
    // PUT /api/v1/operation/profiles/{profileId}/geographic-exposure
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/geographic-exposure")
    public ResponseEntity<ProfileDetailResponse> saveGeographicExposure(
            @PathVariable Long profileId,
            @Valid @RequestBody GeographicExposureRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/geographic-exposure - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveGeographicExposure(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 9 — RELATED FIRs
    // PUT /api/v1/operation/profiles/{profileId}/related-firs
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/related-firs")
    public ResponseEntity<ProfileDetailResponse> saveRelatedFIRs(
            @PathVariable Long profileId,
            @Valid @RequestBody RelatedFIRsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/related-firs - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveRelatedFIRs(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 10 — MATERIAL SEIZED
    // PUT /api/v1/operation/profiles/{profileId}/material-seized
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/material-seized")
    public ResponseEntity<ProfileDetailResponse> saveMaterialSeized(
            @PathVariable Long profileId,
            @Valid @RequestBody MaterialSeizedRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/material-seized - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveMaterialSeized(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 11 — ASSETS (VEHICLES)
    // PUT /api/v1/operation/profiles/{profileId}/assets
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/assets")
    public ResponseEntity<ProfileDetailResponse> saveAssets(
            @PathVariable Long profileId,
            @Valid @RequestBody AssetsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/assets - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveAssets(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 12 — KNOWN ASSOCIATES
    // PUT /api/v1/operation/profiles/{profileId}/known-associates
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/known-associates")
    public ResponseEntity<ProfileDetailResponse> saveKnownAssociates(
            @PathVariable Long profileId,
            @Valid @RequestBody KnownAssociatesRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/known-associates - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveKnownAssociates(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 13 — KNOWN EMPLOYEES
    // PUT /api/v1/operation/profiles/{profileId}/known-employees
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/known-employees")
    public ResponseEntity<ProfileDetailResponse> saveKnownEmployees(
            @PathVariable Long profileId,
            @Valid @RequestBody KnownEmployeesRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/known-employees - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveKnownEmployees(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 14 — PRODUCTS & OPERATIONS
    // PUT /api/v1/operation/profiles/{profileId}/products-operations
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/products-operations")
    public ResponseEntity<ProfileDetailResponse> saveProductsOperations(
            @PathVariable Long profileId,
            @Valid @RequestBody ProductsOperationsRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/products-operations - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveProductsOperations(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 15 — FAMILY BACKGROUND
    // PUT /api/v1/operation/profiles/{profileId}/family-background
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/family-background")
    public ResponseEntity<ProfileDetailResponse> saveFamilyBackground(
            @PathVariable Long profileId,
            @Valid @RequestBody FamilyBackgroundRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/family-background - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveFamilyBackground(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 16 — INFLUENTIAL LINKS
    // PUT /api/v1/operation/profiles/{profileId}/influential-links
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/influential-links")
    public ResponseEntity<ProfileDetailResponse> saveInfluentialLinks(
            @PathVariable Long profileId,
            @Valid @RequestBody InfluentialLinksRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/influential-links - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveInfluentialLinks(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 17 — CURRENT STATUS
    // PUT /api/v1/operation/profiles/{profileId}/current-status
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/current-status")
    public ResponseEntity<ProfileDetailResponse> saveCurrentStatus(
            @PathVariable Long profileId,
            @Valid @RequestBody CurrentStatusRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/current-status - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveCurrentStatus(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 18 — ADDITIONAL INFO
    // PUT /api/v1/operation/profiles/{profileId}/additional-info
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{profileId}/additional-info")
    public ResponseEntity<ProfileDetailResponse> saveAdditionalInfo(
            @PathVariable Long profileId,
            @Valid @RequestBody AdditionalInfoRequest request,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("PUT /api/v1/operation/profiles/{}/additional-info - empId: {}", profileId, empId);
        ProfileDetailResponse response = profileService.saveAdditionalInfo(profileId, request, empId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ALL PROFILES (Paginated)
    // GET /api/v1/operation/profiles?page=0&size=10&sortBy=createdAt&direction=desc
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<PagedProfileResponse> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("GET /api/v1/operation/profiles - page: {}, size: {}", page, size);
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedProfileResponse response = profileService.getAllProfiles(pageable);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH PROFILES
    // GET /api/v1/operation/profiles/search?query=john&page=0&size=10
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/search")
    public ResponseEntity<PagedProfileResponse> searchProfiles(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/v1/operation/profiles/search - query: {}", query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedProfileResponse response = profileService.searchProfiles(query, pageable);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET BY STATUS
    // GET /api/v1/operation/profiles/by-status?status=ACTIVE&page=0&size=10
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/by-status")
    public ResponseEntity<PagedProfileResponse> getByStatus(
            @RequestParam ProfileStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/v1/operation/profiles/by-status - status: {}", status);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedProfileResponse response = profileService.getProfilesByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET PROFILE BY ID
    // GET /api/v1/operation/profiles/{profileId}
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDetailResponse> getProfileById(
            @PathVariable Long profileId) {

        log.info("GET /api/v1/operation/profiles/{}", profileId);
        ProfileDetailResponse response = profileService.getProfileById(profileId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET STEP STATUSES
    // GET /api/v1/operation/profiles/{profileId}/steps
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{profileId}/steps")
    public ResponseEntity<List<StepStatusResponse>> getStepStatuses(
            @PathVariable Long profileId) {

        log.info("GET /api/v1/operation/profiles/{}/steps", profileId);
        List<StepStatusResponse> response = profileService.getStepStatuses(profileId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET CHANGE LOG
    // GET /api/v1/operation/profiles/{profileId}/change-log?page=0&size=20
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{profileId}/change-log")
    public ResponseEntity<ChangeLogPagedResponse> getChangeLog(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/v1/operation/profiles/{}/change-log", profileId);
        Pageable pageable = PageRequest.of(page, size);
        ChangeLogPagedResponse response = profileService.getChangeLog(profileId, pageable);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SOFT DELETE
    // DELETE /api/v1/operation/profiles/{profileId}
    // ─────────────────────────────────────────────────────────────────────────

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable Long profileId,
            Authentication authentication) {

        String empId = authentication.getName();
        log.info("DELETE /api/v1/operation/profiles/{} - empId: {}", profileId, empId);
        profileService.deleteProfile(profileId, empId);
        return ResponseEntity.noContent().build();
    }
}
