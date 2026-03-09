package com.tbcpl.workforce.operation.profile.service;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.service.EmployeeService;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.operation.profile.dto.request.*;
import com.tbcpl.workforce.operation.profile.dto.response.*;
import com.tbcpl.workforce.operation.profile.entity.*;
import com.tbcpl.workforce.operation.profile.enums.*;
import com.tbcpl.workforce.operation.profile.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.tbcpl.workforce.common.util.CloudinaryService;




import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpProfileServiceImpl implements OpProfileService {

    private final OpProfileRepository profileRepository;
    private final OpProfilePersonalInfoRepository personalInfoRepository;
    private final OpProfilePhysicalAttributesRepository physicalAttributesRepository;
    private final OpProfileAddressRepository addressRepository;
    private final OpProfileContactInfoRepository contactInfoRepository;
    private final OpProfileIdentificationDocsRepository identificationDocsRepository;
    private final OpProfileBusinessActivitiesRepository businessActivitiesRepository;
    private final OpProfileGeographicExposureRepository geographicExposureRepository;
    private final OpProfileAssociatedCompanyRepository associatedCompanyRepository;
    private final OpProfileFirRepository firRepository;
    private final OpProfileMaterialSeizedRepository materialSeizedRepository;
    private final OpProfileVehicleRepository vehicleRepository;
    private final OpProfileAssociateRepository associateRepository;
    private final OpProfileInfluentialLinkRepository influentialLinkRepository;
    private final OpProfileProductInfringedRepository productInfringedRepository;
    private final OpProfileProductsOperationsRepository productsOperationsRepository;
    private final OpProfileFamilyBackgroundRepository familyBackgroundRepository;
    private final OpProfileSiblingRepository siblingRepository;
    private final OpProfileCurrentStatusRepository currentStatusRepository;
    private final OpProfileAdditionalInfoRepository additionalInfoRepository;
    private final OpProfileStepStatusRepository stepStatusRepository;
    private final OpProfileChangeLogRepository changeLogRepository;
    private final EmployeeService employeeService;
    private final CloudinaryService cloudinaryService;

    private static final int TOTAL_STEPS = 16;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ─────────────────────────────────────────────────────────────────────────
    // AUTHORIZATION CHECK
    // ─────────────────────────────────────────────────────────────────────────

    private Employee validateAndGetEmployee(String empId) {
        Employee employee = employeeService.getEmployeeEntityByEmpId(empId);
        String dept = employee.getDepartment().getDepartmentName().toUpperCase();
        String role = employee.getRole().getRoleName().toUpperCase();

        boolean deptAllowed = dept.equals("ADMIN") || dept.equals("OPERATION");
        boolean roleAllowed = role.equals("STAFF") || role.equals("ASSOCIATE");

        if (!deptAllowed || !roleAllowed) {
            log.warn("Access denied for empId: {} dept: {} role: {}", empId, dept, role);
            throw new AccessDeniedException(
                    "Only ADMIN/OPERATION department employees with STAFF/ASSOCIATE role can manage profiles"
            );
        }
        return employee;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROFILE NUMBER GENERATION
    // ─────────────────────────────────────────────────────────────────────────

    private synchronized String generateProfileNumber() {
        Optional<String> latest = profileRepository.findLatestProfileNumber();
        int nextNumber = 1;
        if (latest.isPresent()) {
            String lastNumber = latest.get().replace("OFPRF-", "");
            nextNumber = Integer.parseInt(lastNumber) + 1;
        }
        return String.format("OFPRF-%03d", nextNumber);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 1 — INIT PROFILE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse initProfile(ProfileInitRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        log.info("Initializing profile by empId: {}", empId);

        String profileNumber = generateProfileNumber();
        String fullName = buildFullName(request.getFirstName(), request.getMiddleName(), request.getLastName());

        OpProfile profile = OpProfile.builder()
                .profileNumber(profileNumber)
                .name(fullName)
                .status(ProfileStatus.ACTIVE)
                .createdBy(empId)
                .updatedBy(empId)
                .build();

        profile = profileRepository.save(profile);

        // Save personal info
        OpProfilePersonalInfo personalInfo = OpProfilePersonalInfo.builder()
                .profile(profile)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .gender(request.getGender())
                .middleName(request.getMiddleName())
                .dateOfBirth(parseDate(request.getDateOfBirth()))
                .bloodGroup(request.getBloodGroup())
                .nationality(request.getNationality())
                .profilePhoto(request.getProfilePhoto())
                .build();

        personalInfoRepository.save(personalInfo);

        // Initialize all 16 step statuses as NOT_FILLED
        initializeStepStatuses(profile);

        // Update step 1 status
        updateStepStatus(profile.getId(), 1, "PERSONAL_INFO", evaluateStep1Status(request));

        // Log creation
        saveChangeLog(profile, empId, employee.getFullName(), "PERSONAL_INFO",
                ChangeAction.CREATED, "profile", null, profileNumber);

        log.info("Profile created: {} by {}", profileNumber, empId);
        return buildProfileDetailResponse(profile.getId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 2 — PHYSICAL ATTRIBUTES
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse savePhysicalAttributes(Long profileId, PhysicalAttributesRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfilePhysicalAttributes entity = physicalAttributesRepository
                .findByProfileId(profileId)
                .orElse(OpProfilePhysicalAttributes.builder().profile(profile).build());

        entity.setHeight(request.getHeight());
        entity.setWeight(request.getWeight());
        entity.setEyeColor(request.getEyeColor());
        entity.setHairColor(request.getHairColor());
        entity.setSkinTone(request.getSkinTone());
        entity.setIdentificationMarks(request.getIdentificationMarks());
        entity.setDisabilities(request.getDisabilities());

        physicalAttributesRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 2, "PHYSICAL_ATTRIBUTES", evaluatePhysicalStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "PHYSICAL_ATTRIBUTES",
                ChangeAction.UPDATED, "physical_attributes", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 3 — ADDRESS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveAddress(Long profileId, AddressRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileAddress entity = addressRepository
                .findByProfileId(profileId)
                .orElse(OpProfileAddress.builder().profile(profile).build());

        entity.setAddressLine1(request.getAddressLine1());
        entity.setAddressLine2(request.getAddressLine2());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setPincode(request.getPincode());
        entity.setCountry(request.getCountry());
        entity.setPermanentSameAsCurrent(request.getPermanentSameAsCurrent());

        if (Boolean.TRUE.equals(request.getPermanentSameAsCurrent())) {
            entity.setPermAddressLine1(request.getAddressLine1());
            entity.setPermAddressLine2(request.getAddressLine2());
            entity.setPermCity(request.getCity());
            entity.setPermState(request.getState());
            entity.setPermPincode(request.getPincode());
            entity.setPermCountry(request.getCountry());
        } else {
            entity.setPermAddressLine1(request.getPermAddressLine1());
            entity.setPermAddressLine2(request.getPermAddressLine2());
            entity.setPermCity(request.getPermCity());
            entity.setPermState(request.getPermState());
            entity.setPermPincode(request.getPermPincode());
            entity.setPermCountry(request.getPermCountry());
        }

        addressRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 3, "ADDRESS", evaluateAddressStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "ADDRESS",
                ChangeAction.UPDATED, "address", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 4 — CONTACT INFO
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveContactInfo(Long profileId, ContactInfoRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileContactInfo entity = contactInfoRepository
                .findByProfileId(profileId)
                .orElse(OpProfileContactInfo.builder().profile(profile).build());

        entity.setPrimaryPhone(request.getPrimaryPhone());
        entity.setSecondaryPhone(request.getSecondaryPhone());
        entity.setPrimaryEmail(request.getPrimaryEmail());
        entity.setSecondaryEmail(request.getSecondaryEmail());
        entity.setEmergencyContactName(request.getEmergencyContactName());
        entity.setEmergencyContactPhone(request.getEmergencyContactPhone());
        entity.setEmergencyContactRelation(request.getEmergencyContactRelation());

        contactInfoRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 4, "CONTACT_INFO", evaluateContactStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "CONTACT_INFO",
                ChangeAction.UPDATED, "contact_info", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 5 — IDENTIFICATION DOCS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveIdentificationDocs(Long profileId, IdentificationDocsRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileIdentificationDocs entity = identificationDocsRepository
                .findByProfileId(profileId)
                .orElse(OpProfileIdentificationDocs.builder().profile(profile).build());

        entity.setEmployeeId(request.getEmployeeId());
        entity.setAadhaarNumber(request.getAadhaarNumber());
        entity.setAadhaarPhoto(request.getAadhaarPhoto());
        entity.setPanNumber(request.getPanNumber());
        entity.setPanPhoto(request.getPanPhoto());
        entity.setDrivingLicense(request.getDrivingLicense());
        entity.setDlPhoto(request.getDlPhoto());
        entity.setPassportNumber(request.getPassportNumber());
        entity.setPassportPhoto(request.getPassportPhoto());
        entity.setOtherIdType(request.getOtherIdType());
        entity.setOtherIdNumber(request.getOtherIdNumber());
        entity.setOtherIdPhoto(request.getOtherIdPhoto());

        identificationDocsRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 5, "IDENTIFICATION_DOCS", evaluateIdentificationStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "IDENTIFICATION_DOCS",
                ChangeAction.UPDATED, "identification_docs", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 6 — BUSINESS ACTIVITIES
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveBusinessActivities(Long profileId, BusinessActivitiesRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileBusinessActivities entity = businessActivitiesRepository
                .findByProfileId(profileId)
                .orElse(OpProfileBusinessActivities.builder().profile(profile).build());

        entity.setRetailerStatus(request.getRetailerStatus());
        entity.setRetailerType(request.getRetailerType());
        entity.setRetailerDetails(request.getRetailerDetails());
        entity.setSupplierStatus(request.getSupplierStatus());
        entity.setSupplierType(request.getSupplierType());
        entity.setSupplierDetails(request.getSupplierDetails());
        entity.setManufacturerStatus(request.getManufacturerStatus());
        entity.setManufacturerType(request.getManufacturerType());
        entity.setManufacturerDetails(request.getManufacturerDetails());

        businessActivitiesRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 6, "BUSINESS_ACTIVITIES", evaluateBusinessStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "BUSINESS_ACTIVITIES",
                ChangeAction.UPDATED, "business_activities", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 7 — ENTITY & ORGANIZATION
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveEntityOrganization(Long profileId, EntityOrganizationRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        // Delete existing and replace (clean upsert for list)
        associatedCompanyRepository.deleteByProfileId(profileId);

        List<OpProfileAssociatedCompany> companies = request.getAssociatedCompanies().stream()
                .filter(c -> c.getCompanyName() != null && !c.getCompanyName().isBlank())
                .map(c -> OpProfileAssociatedCompany.builder()
                        .profile(profile)
                        .companyName(c.getCompanyName())
                        .relationshipNature(c.getRelationshipNature())
                        .details(c.getDetails())
                        .build())
                .toList();

        associatedCompanyRepository.saveAll(companies);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 7, "ENTITY_ORGANIZATION",
                companies.isEmpty() ? StepStatus.NOT_FILLED : StepStatus.COMPLETED);
        saveChangeLog(profile, empId, employee.getFullName(), "ENTITY_ORGANIZATION",
                ChangeAction.UPDATED, "associated_companies", null, companies.size() + " companies saved");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 8 — GEOGRAPHIC EXPOSURE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveGeographicExposure(Long profileId, GeographicExposureRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileGeographicExposure entity = geographicExposureRepository
                .findByProfileId(profileId)
                .orElse(OpProfileGeographicExposure.builder().profile(profile).build());

        entity.setOperatingRegions(request.getOperatingRegions());
        entity.setMarkets(request.getMarkets());
        entity.setJurisdictions(request.getJurisdictions());

        geographicExposureRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 8, "GEOGRAPHIC_EXPOSURE", evaluateGeoStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "GEOGRAPHIC_EXPOSURE",
                ChangeAction.UPDATED, "geographic_exposure", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 9 — RELATED FIRs
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveRelatedFIRs(Long profileId, RelatedFIRsRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        firRepository.deleteByProfileId(profileId);

        List<OpProfileFir> firs = request.getFirs().stream()
                .filter(f -> f.getFirNumber() != null && !f.getFirNumber().isBlank())
                .map(f -> OpProfileFir.builder()
                        .profile(profile)
                        .firNumber(f.getFirNumber())
                        .caseNumber(f.getCaseNumber())
                        .sections(f.getSections())
                        .dateRegistered(parseDate(f.getDateRegistered()))
                        .status(f.getStatus())
                        .build())
                .toList();

        firRepository.saveAll(firs);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 9, "RELATED_FIRS",
                firs.isEmpty() ? StepStatus.NOT_FILLED : StepStatus.COMPLETED);
        saveChangeLog(profile, empId, employee.getFullName(), "RELATED_FIRS",
                ChangeAction.UPDATED, "firs", null, firs.size() + " FIRs saved");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 10 — MATERIAL SEIZED
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveMaterialSeized(Long profileId, MaterialSeizedRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        materialSeizedRepository.deleteByProfileId(profileId);

        List<OpProfileMaterialSeized> items = request.getMaterialSeized().stream()
                .filter(m -> m.getBrandName() != null && !m.getBrandName().isBlank())
                .map(m -> OpProfileMaterialSeized.builder()
                        .profile(profile)
                        .brandName(m.getBrandName())
                        .company(m.getCompany())
                        .quantity(m.getQuantity())
                        .location(m.getLocation())
                        .raidingAuthority(m.getRaidingAuthority())
                        .raidingAuthorityOther(m.getRaidingAuthorityOther())
                        .dateSeized(parseDate(m.getDateSeized()))
                        .build())
                .toList();

        materialSeizedRepository.saveAll(items);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 10, "MATERIAL_SEIZED",
                items.isEmpty() ? StepStatus.NOT_FILLED : StepStatus.COMPLETED);
        saveChangeLog(profile, empId, employee.getFullName(), "MATERIAL_SEIZED",
                ChangeAction.UPDATED, "material_seized", null, items.size() + " items saved");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 11 — ASSETS (VEHICLES)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveAssets(Long profileId, AssetsRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        vehicleRepository.deleteByProfileId(profileId);

        List<OpProfileVehicle> vehicles = request.getVehicles().stream()
                .filter(v -> v.getMake() != null && !v.getMake().isBlank())
                .map(v -> OpProfileVehicle.builder()
                        .profile(profile)
                        .make(v.getMake())
                        .model(v.getModel())
                        .registrationNumber(v.getRegistrationNumber())
                        .ownershipType(v.getOwnershipType())
                        .build())
                .toList();

        vehicleRepository.saveAll(vehicles);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 11, "ASSETS",
                vehicles.isEmpty() ? StepStatus.NOT_FILLED : StepStatus.COMPLETED);
        saveChangeLog(profile, empId, employee.getFullName(), "ASSETS",
                ChangeAction.UPDATED, "vehicles", null, vehicles.size() + " vehicles saved");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 12 — KNOWN ASSOCIATES
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveKnownAssociates(Long profileId, KnownAssociatesRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        // Delete only ASSOCIATE/FAMILY role entries, keep EMPLOYEE
        List<OpProfileAssociate> existing = associateRepository.findByProfileId(profileId);
        List<Long> toDelete = existing.stream()
                .filter(a -> a.getRole() != AssociateRole.EMPLOYEE)
                .map(OpProfileAssociate::getId)
                .toList();
        associateRepository.deleteAllById(toDelete);

        List<OpProfileAssociate> associates = request.getKnownAssociates().stream()
                .filter(a -> a.getName() != null && !a.getName().isBlank())
                .map(a -> OpProfileAssociate.builder()
                        .profile(profile)
                        .name(a.getName())
                        .relationship(a.getRelationship())
                        .role(a.getRole() != null ? a.getRole() : AssociateRole.ASSOCIATE)
                        .contactInfo(a.getContactInfo())
                        .notes(a.getNotes())
                        .build())
                .toList();

        associateRepository.saveAll(associates);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 12, "KNOWN_ASSOCIATES",
                associates.isEmpty() ? StepStatus.NOT_FILLED : StepStatus.COMPLETED);
        saveChangeLog(profile, empId, employee.getFullName(), "KNOWN_ASSOCIATES",
                ChangeAction.UPDATED, "associates", null, associates.size() + " associates saved");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 13 — KNOWN EMPLOYEES
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveKnownEmployees(Long profileId, KnownEmployeesRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        // Delete only EMPLOYEE role entries
        List<OpProfileAssociate> existing = associateRepository.findByProfileId(profileId);
        List<Long> toDelete = existing.stream()
                .filter(a -> a.getRole() == AssociateRole.EMPLOYEE)
                .map(OpProfileAssociate::getId)
                .toList();
        associateRepository.deleteAllById(toDelete);

        List<OpProfileAssociate> employees = request.getKnownEmployees().stream()
                .filter(e -> e.getName() != null && !e.getName().isBlank())
                .map(e -> OpProfileAssociate.builder()
                        .profile(profile)
                        .name(e.getName())
                        .relationship(e.getRelationship())
                        .role(e.getRole() != null ? e.getRole() : AssociateRole.EMPLOYEE)
                        .contactInfo(e.getContactInfo())
                        .notes(e.getNotes())
                        .build())
                .toList();

        associateRepository.saveAll(employees);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 13, "KNOWN_EMPLOYEES",
                employees.isEmpty() ? StepStatus.NOT_FILLED : StepStatus.COMPLETED);
        saveChangeLog(profile, empId, employee.getFullName(), "KNOWN_EMPLOYEES",
                ChangeAction.UPDATED, "employees", null, employees.size() + " employees saved");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 14 — PRODUCTS & OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveProductsOperations(Long profileId, ProductsOperationsRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        // Save products infringed
        productInfringedRepository.deleteByProfileId(profileId);
        List<OpProfileProductInfringed> products = request.getProductsInfringed().stream()
                .filter(p -> p.getBrandName() != null && !p.getBrandName().isBlank())
                .map(p -> OpProfileProductInfringed.builder()
                        .profile(profile)
                        .brandName(p.getBrandName())
                        .companyName(p.getCompanyName())
                        .productType(p.getProductType())
                        .build())
                .toList();
        productInfringedRepository.saveAll(products);

        // Save operations details
        OpProfileProductsOperations ops = productsOperationsRepository
                .findByProfileId(profileId)
                .orElse(OpProfileProductsOperations.builder().profile(profile).build());
        ops.setKnownModusOperandi(request.getKnownModusOperandi());
        ops.setKnownLocations(request.getKnownLocations());
        productsOperationsRepository.save(ops);

        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 14, "PRODUCTS_OPERATIONS",
                evaluateProductsOpsStep(request, products));
        saveChangeLog(profile, empId, employee.getFullName(), "PRODUCTS_OPERATIONS",
                ChangeAction.UPDATED, "products_operations", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 15 — FAMILY BACKGROUND
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveFamilyBackground(Long profileId, FamilyBackgroundRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileFamilyBackground entity = familyBackgroundRepository
                .findByProfileId(profileId)
                .orElse(OpProfileFamilyBackground.builder().profile(profile).build());

        entity.setFatherName(request.getFatherName());
        entity.setFatherOccupation(request.getFatherOccupation());
        entity.setFatherContact(request.getFatherContact());
        entity.setMotherName(request.getMotherName());
        entity.setMotherOccupation(request.getMotherOccupation());
        entity.setMotherContact(request.getMotherContact());
        familyBackgroundRepository.save(entity);

        // Save siblings
        siblingRepository.deleteByProfileId(profileId);
        List<OpProfileSibling> siblings = request.getSiblings().stream()
                .filter(s -> s.getName() != null && !s.getName().isBlank())
                .map(s -> OpProfileSibling.builder()
                        .profile(profile)
                        .name(s.getName())
                        .relationship(s.getRelationship())
                        .occupation(s.getOccupation())
                        .build())
                .toList();
        siblingRepository.saveAll(siblings);

        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 15, "FAMILY_BACKGROUND", evaluateFamilyStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "FAMILY_BACKGROUND",
                ChangeAction.UPDATED, "family_background", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 15b — INFLUENTIAL LINKS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveInfluentialLinks(Long profileId, InfluentialLinksRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        influentialLinkRepository.deleteByProfileId(profileId);

        List<OpProfileInfluentialLink> links = request.getInfluentialLinks().stream()
                .filter(l -> l.getPersonName() != null && !l.getPersonName().isBlank())
                .map(l -> OpProfileInfluentialLink.builder()
                        .profile(profile)
                        .personName(l.getPersonName())
                        .profileDetails(l.getProfileDetails())
                        .relationship(l.getRelationship())
                        .build())
                .toList();

        influentialLinkRepository.saveAll(links);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 16, "INFLUENTIAL_LINKS",
                links.isEmpty() ? StepStatus.NOT_FILLED : StepStatus.COMPLETED);
        saveChangeLog(profile, empId, employee.getFullName(), "INFLUENTIAL_LINKS",
                ChangeAction.UPDATED, "influential_links", null, links.size() + " links saved");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 16 — CURRENT STATUS (mapped frontend step 15)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveCurrentStatus(Long profileId, CurrentStatusRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileCurrentStatus entity = currentStatusRepository
                .findByProfileId(profileId)
                .orElse(OpProfileCurrentStatus.builder().profile(profile).build());

        entity.setStatus(request.getStatus());
        entity.setLastKnownLocation(request.getLastKnownLocation());
        entity.setStatusDate(parseDate(request.getStatusDate()));
        entity.setRemarks(request.getRemarks());

        // Mirror status to master profile
        if (request.getStatus() != null) {
            profile.setStatus(request.getStatus());
            profileRepository.save(profile);
        }

        currentStatusRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 17, "CURRENT_STATUS", evaluateCurrentStatusStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "CURRENT_STATUS",
                ChangeAction.UPDATED, "current_status", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 17 — ADDITIONAL INFO (mapped frontend step 16)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileDetailResponse saveAdditionalInfo(Long profileId, AdditionalInfoRequest request, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);

        OpProfileAdditionalInfo entity = additionalInfoRepository
                .findByProfileId(profileId)
                .orElse(OpProfileAdditionalInfo.builder().profile(profile).build());

        entity.setNotes(request.getNotes());
        entity.setBehavioralNotes(request.getBehavioralNotes());
        entity.setRiskLevel(request.getRiskLevel());
        entity.setTags(request.getTags());
        entity.setAdditionalPhotos(request.getAdditionalPhotos());
        entity.setAttachments(request.getAttachments());
        entity.setLinkedCases(request.getLinkedCases());

        additionalInfoRepository.save(entity);
        updateProfileUpdatedBy(profile, empId);
        updateStepStatus(profileId, 18, "ADDITIONAL_INFO", evaluateAdditionalStep(request));
        saveChangeLog(profile, empId, employee.getFullName(), "ADDITIONAL_INFO",
                ChangeAction.UPDATED, "additional_info", null, "updated");

        return buildProfileDetailResponse(profileId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ OPERATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PagedProfileResponse getAllProfiles(Pageable pageable) {
        Page<OpProfile> page = profileRepository.findAllActive(pageable);
        return mapToPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedProfileResponse searchProfiles(String search, Pageable pageable) {
        Page<OpProfile> page = profileRepository.searchByNameOrProfileNumber(search, pageable);
        return mapToPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedProfileResponse getProfilesByStatus(ProfileStatus status, Pageable pageable) {
        Page<OpProfile> page = profileRepository.findByStatus(status, pageable);
        return mapToPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDetailResponse getProfileById(Long profileId) {
        return buildProfileDetailResponse(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StepStatusResponse> getStepStatuses(Long profileId) {
        return stepStatusRepository.findByProfileIdOrderByStepNumber(profileId)
                .stream()
                .map(s -> StepStatusResponse.builder()
                        .stepNumber(s.getStepNumber())
                        .stepName(s.getStepName())
                        .status(s.getStatus())
                        .updatedAt(s.getUpdatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogPagedResponse getChangeLog(Long profileId, Pageable pageable) {
        Page<OpProfileChangeLog> page = changeLogRepository
                .findByProfileIdOrderByChangedAtDesc(profileId, pageable);
        List<ChangeLogResponse> logs = page.getContent().stream()
                .map(l -> ChangeLogResponse.builder()
                        .id(l.getId())
                        .changedBy(l.getChangedBy())
                        .changedByName(l.getChangedByName())
                        .changedAt(l.getChangedAt())
                        .stepName(l.getStepName())
                        .action(l.getAction())
                        .fieldName(l.getFieldName())
                        .oldValue(l.getOldValue())
                        .newValue(l.getNewValue())
                        .build())
                .toList();

        return ChangeLogPagedResponse.builder()
                .logs(logs)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SOFT DELETE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteProfile(Long profileId, String empId) {
        Employee employee = validateAndGetEmployee(empId);
        OpProfile profile = getActiveProfile(profileId);
        profile.setIsDeleted(true);
        profile.setUpdatedBy(empId);
        profileRepository.save(profile);
        saveChangeLog(profile, empId, employee.getFullName(), "PROFILE",
                ChangeAction.DELETED, "is_deleted", "false", "true");
        log.info("Profile soft deleted: {} by {}", profile.getProfileNumber(), empId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // IMAGE UPLOAD
    // ─────────────────────────────────────────────────────────────────────────

    // ✅ FIXED — uses existing uploadFile method + maps to ImageUploadResponse
    @Override
    public ImageUploadResponse uploadImage(MultipartFile file, String folder) {
        try {
            Map<String, String> result = cloudinaryService.uploadFile(file,
                    folder != null ? folder : "profiles");

            return ImageUploadResponse.builder()
                    .url(result.get("url"))
                    .publicId(result.get("public_id"))
                    .format(file.getContentType())
                    .size(file.getSize())
                    .build();
        } catch (java.io.IOException e) {
            log.error("Image upload failed", e);
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }


    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private OpProfile getActiveProfile(Long profileId) {
        return profileRepository.findById(profileId)
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found with id: " + profileId));
    }

    private void updateProfileUpdatedBy(OpProfile profile, String empId) {
        profile.setUpdatedBy(empId);
        profileRepository.save(profile);
    }

    private void initializeStepStatuses(OpProfile profile) {
        String[] stepNames = {
                "PERSONAL_INFO", "PHYSICAL_ATTRIBUTES", "ADDRESS", "CONTACT_INFO",
                "IDENTIFICATION_DOCS", "BUSINESS_ACTIVITIES", "ENTITY_ORGANIZATION",
                "GEOGRAPHIC_EXPOSURE", "RELATED_FIRS", "MATERIAL_SEIZED", "ASSETS",
                "KNOWN_ASSOCIATES", "KNOWN_EMPLOYEES", "PRODUCTS_OPERATIONS",
                "FAMILY_BACKGROUND", "INFLUENTIAL_LINKS", "CURRENT_STATUS", "ADDITIONAL_INFO"
        };

        List<OpProfileStepStatus> statuses = new ArrayList<>();
        for (int i = 0; i < stepNames.length; i++) {
            statuses.add(OpProfileStepStatus.builder()
                    .profile(profile)
                    .stepNumber(i + 1)
                    .stepName(stepNames[i])
                    .status(StepStatus.NOT_FILLED)
                    .build());
        }
        stepStatusRepository.saveAll(statuses);
    }

    private void updateStepStatus(Long profileId, int stepNumber, String stepName, StepStatus status) {
        OpProfileStepStatus stepStatus = stepStatusRepository
                .findByProfileIdAndStepNumber(profileId, stepNumber)
                .orElse(OpProfileStepStatus.builder()
                        .profile(profileRepository.getReferenceById(profileId))
                        .stepNumber(stepNumber)
                        .stepName(stepName)
                        .build());
        stepStatus.setStatus(status);
        stepStatusRepository.save(stepStatus);
    }

    private void saveChangeLog(OpProfile profile, String empId, String empName,
                               String stepName, ChangeAction action,
                               String fieldName, String oldValue, String newValue) {
        OpProfileChangeLog log = OpProfileChangeLog.builder()
                .profile(profile)
                .changedBy(empId)
                .changedByName(empName)
                .stepName(stepName)
                .action(action)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        changeLogRepository.save(log);
    }

    private String buildFullName(String first, String middle, String last) {
        StringBuilder name = new StringBuilder(first.trim());
        if (middle != null && !middle.isBlank()) name.append(" ").append(middle.trim());
        name.append(" ").append(last.trim());
        return name.toString();
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("Invalid date format: {}", dateStr);
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP STATUS EVALUATORS
    // ─────────────────────────────────────────────────────────────────────────

    private StepStatus evaluateStep1Status(ProfileInitRequest r) {
        long optionalFilled = countNonNull(r.getMiddleName(), r.getDateOfBirth(),
                r.getBloodGroup(), r.getNationality(), r.getProfilePhoto());
        if (optionalFilled >= 1) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED; // mandatory 3 filled but no optional
    }

    private StepStatus evaluatePhysicalStep(PhysicalAttributesRequest r) {
        long filled = countNonNull(r.getHeight(), r.getWeight(), r.getEyeColor(),
                r.getHairColor(), r.getSkinTone(), r.getIdentificationMarks(), r.getDisabilities());
        if (filled == 0) return StepStatus.NOT_FILLED;
        if (filled == 7) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateAddressStep(AddressRequest r) {
        long filled = countNonNull(r.getAddressLine1(), r.getCity(), r.getState(),
                r.getPincode(), r.getCountry());
        if (filled == 0) return StepStatus.NOT_FILLED;
        if (filled == 5) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateContactStep(ContactInfoRequest r) {
        long filled = countNonNull(r.getPrimaryPhone(), r.getSecondaryPhone(),
                r.getPrimaryEmail(), r.getSecondaryEmail(), r.getEmergencyContactName(),
                r.getEmergencyContactPhone(), r.getEmergencyContactRelation());
        if (filled == 0) return StepStatus.NOT_FILLED;
        if (filled == 7) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateIdentificationStep(IdentificationDocsRequest r) {
        long filled = countNonNull(r.getAadhaarNumber(), r.getPanNumber(),
                r.getDrivingLicense(), r.getPassportNumber(), r.getOtherIdNumber());
        if (filled == 0) return StepStatus.NOT_FILLED;
        if (filled >= 3) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateBusinessStep(BusinessActivitiesRequest r) {
        long filled = countNonNull(r.getRetailerStatus(), r.getSupplierStatus(), r.getManufacturerStatus());
        if (filled == 0) return StepStatus.NOT_FILLED;
        if (filled == 3) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateGeoStep(GeographicExposureRequest r) {
        boolean anyFilled = !r.getOperatingRegions().isEmpty()
                || !r.getMarkets().isEmpty()
                || !r.getJurisdictions().isEmpty();
        boolean allFilled = !r.getOperatingRegions().isEmpty()
                && !r.getMarkets().isEmpty()
                && !r.getJurisdictions().isEmpty();
        if (!anyFilled) return StepStatus.NOT_FILLED;
        if (allFilled) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateProductsOpsStep(ProductsOperationsRequest r,
                                               List<OpProfileProductInfringed> saved) {
        boolean hasProducts = !saved.isEmpty();
        boolean hasModus = r.getKnownModusOperandi() != null && !r.getKnownModusOperandi().isBlank();
        if (!hasProducts && !hasModus) return StepStatus.NOT_FILLED;
        if (hasProducts && hasModus) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateFamilyStep(FamilyBackgroundRequest r) {
        long filled = countNonNull(r.getFatherName(), r.getMotherName());
        if (filled == 0) return StepStatus.NOT_FILLED;
        if (filled == 2) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateCurrentStatusStep(CurrentStatusRequest r) {
        if (r.getStatus() == null && r.getStatusDate() == null) return StepStatus.NOT_FILLED;
        if (r.getStatus() != null && r.getStatusDate() != null) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private StepStatus evaluateAdditionalStep(AdditionalInfoRequest r) {
        boolean hasNotes = r.getNotes() != null && !r.getNotes().isBlank();
        boolean hasRisk = r.getRiskLevel() != null;
        if (!hasNotes && !hasRisk) return StepStatus.NOT_FILLED;
        if (hasNotes && hasRisk) return StepStatus.COMPLETED;
        return StepStatus.HALF_FILLED;
    }

    private long countNonNull(Object... fields) {
        long count = 0;
        for (Object f : fields) {
            if (f instanceof String s) {
                if (!s.isBlank()) count++;
            } else if (f != null) {
                count++;
            }
        }
        return count;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RESPONSE BUILDER
    // ─────────────────────────────────────────────────────────────────────────

    private ProfileDetailResponse buildProfileDetailResponse(Long profileId) {
        OpProfile profile = getActiveProfile(profileId);

        // Personal Info
        PersonalInfoResponse personalInfoResponse = personalInfoRepository
                .findByProfileId(profileId)
                .map(p -> PersonalInfoResponse.builder()
                        .firstName(p.getFirstName())
                        .middleName(p.getMiddleName())
                        .lastName(p.getLastName())
                        .gender(p.getGender())
                        .dateOfBirth(p.getDateOfBirth())
                        .bloodGroup(p.getBloodGroup())
                        .nationality(p.getNationality())
                        .profilePhoto(p.getProfilePhoto())
                        .build())
                .orElse(null);

        // Physical Attributes
        PhysicalAttributesResponse physicalResponse = physicalAttributesRepository
                .findByProfileId(profileId)
                .map(p -> PhysicalAttributesResponse.builder()
                        .height(p.getHeight()).weight(p.getWeight())
                        .eyeColor(p.getEyeColor()).hairColor(p.getHairColor())
                        .skinTone(p.getSkinTone())
                        .identificationMarks(p.getIdentificationMarks())
                        .disabilities(p.getDisabilities())
                        .build())
                .orElse(null);

        // Address
        AddressResponse addressResponse = addressRepository
                .findByProfileId(profileId)
                .map(a -> AddressResponse.builder()
                        .addressLine1(a.getAddressLine1()).addressLine2(a.getAddressLine2())
                        .city(a.getCity()).state(a.getState())
                        .pincode(a.getPincode()).country(a.getCountry())
                        .permanentSameAsCurrent(a.getPermanentSameAsCurrent())
                        .permAddressLine1(a.getPermAddressLine1())
                        .permAddressLine2(a.getPermAddressLine2())
                        .permCity(a.getPermCity()).permState(a.getPermState())
                        .permPincode(a.getPermPincode()).permCountry(a.getPermCountry())
                        .build())
                .orElse(null);

        // Contact Info
        ContactInfoResponse contactResponse = contactInfoRepository
                .findByProfileId(profileId)
                .map(c -> ContactInfoResponse.builder()
                        .primaryPhone(c.getPrimaryPhone())
                        .secondaryPhone(c.getSecondaryPhone())
                        .primaryEmail(c.getPrimaryEmail())
                        .secondaryEmail(c.getSecondaryEmail())
                        .emergencyContactName(c.getEmergencyContactName())
                        .emergencyContactPhone(c.getEmergencyContactPhone())
                        .emergencyContactRelation(c.getEmergencyContactRelation())
                        .build())
                .orElse(null);

        // Identification Docs
        IdentificationDocsResponse idDocsResponse = identificationDocsRepository
                .findByProfileId(profileId)
                .map(i -> IdentificationDocsResponse.builder()
                        .employeeId(i.getEmployeeId())
                        .aadhaarNumber(i.getAadhaarNumber()).aadhaarPhoto(i.getAadhaarPhoto())
                        .panNumber(i.getPanNumber()).panPhoto(i.getPanPhoto())
                        .drivingLicense(i.getDrivingLicense()).dlPhoto(i.getDlPhoto())
                        .passportNumber(i.getPassportNumber()).passportPhoto(i.getPassportPhoto())
                        .otherIdType(i.getOtherIdType()).otherIdNumber(i.getOtherIdNumber())
                        .otherIdPhoto(i.getOtherIdPhoto())
                        .build())
                .orElse(null);

        // Business Activities
        BusinessActivitiesResponse businessResponse = businessActivitiesRepository
                .findByProfileId(profileId)
                .map(b -> BusinessActivitiesResponse.builder()
                        .retailerStatus(b.getRetailerStatus()).retailerType(b.getRetailerType())
                        .retailerDetails(b.getRetailerDetails())
                        .supplierStatus(b.getSupplierStatus()).supplierType(b.getSupplierType())
                        .supplierDetails(b.getSupplierDetails())
                        .manufacturerStatus(b.getManufacturerStatus())
                        .manufacturerType(b.getManufacturerType())
                        .manufacturerDetails(b.getManufacturerDetails())
                        .build())
                .orElse(null);

        // Geographic Exposure
        GeographicExposureResponse geoResponse = geographicExposureRepository
                .findByProfileId(profileId)
                .map(g -> GeographicExposureResponse.builder()
                        .operatingRegions(g.getOperatingRegions())
                        .markets(g.getMarkets())
                        .jurisdictions(g.getJurisdictions())
                        .build())
                .orElse(null);

        // Associated Companies
        List<AssociatedCompanyResponse> companies = associatedCompanyRepository
                .findByProfileId(profileId).stream()
                .map(c -> AssociatedCompanyResponse.builder()
                        .id(c.getId()).companyName(c.getCompanyName())
                        .relationshipNature(c.getRelationshipNature()).details(c.getDetails())
                        .build())
                .toList();

        // FIRs
        List<FirResponse> firs = firRepository.findByProfileId(profileId).stream()
                .map(f -> FirResponse.builder()
                        .id(f.getId()).firNumber(f.getFirNumber())
                        .caseNumber(f.getCaseNumber()).sections(f.getSections())
                        .dateRegistered(f.getDateRegistered()).status(f.getStatus())
                        .build())
                .toList();

        // Material Seized
        List<MaterialSeizedItemResponse> materialSeized = materialSeizedRepository
                .findByProfileId(profileId).stream()
                .map(m -> MaterialSeizedItemResponse.builder()
                        .id(m.getId()).brandName(m.getBrandName()).company(m.getCompany())
                        .quantity(m.getQuantity()).location(m.getLocation())
                        .raidingAuthority(m.getRaidingAuthority())
                        .raidingAuthorityOther(m.getRaidingAuthorityOther())
                        .dateSeized(m.getDateSeized())
                        .build())
                .toList();

        // Vehicles
        List<VehicleResponse> vehicles = vehicleRepository.findByProfileId(profileId).stream()
                .map(v -> VehicleResponse.builder()
                        .id(v.getId()).make(v.getMake()).model(v.getModel())
                        .registrationNumber(v.getRegistrationNumber())
                        .ownershipType(v.getOwnershipType())
                        .build())
                .toList();

        // Associates & Employees
        List<AssociateResponse> knownAssociates = associateRepository
                .findByProfileIdAndRole(profileId, AssociateRole.ASSOCIATE).stream()
                .map(this::mapAssociate).toList();

        List<AssociateResponse> knownEmployees = associateRepository
                .findByProfileIdAndRole(profileId, AssociateRole.EMPLOYEE).stream()
                .map(this::mapAssociate).toList();

        // Products Infringed
        List<ProductInfringedResponse> productsInfringed = productInfringedRepository
                .findByProfileId(profileId).stream()
                .map(p -> ProductInfringedResponse.builder()
                        .id(p.getId()).brandName(p.getBrandName())
                        .companyName(p.getCompanyName()).productType(p.getProductType())
                        .build())
                .toList();

        // Products Operations
        ProductsOperationsResponse opsResponse = productsOperationsRepository
                .findByProfileId(profileId)
                .map(o -> ProductsOperationsResponse.builder()
                        .productsInfringed(productsInfringed)
                        .knownModusOperandi(o.getKnownModusOperandi())
                        .knownLocations(o.getKnownLocations())
                        .build())
                .orElse(null);

        // Family Background
        List<SiblingResponse> siblings = siblingRepository.findByProfileId(profileId).stream()
                .map(s -> SiblingResponse.builder()
                        .id(s.getId()).name(s.getName())
                        .relationship(s.getRelationship()).occupation(s.getOccupation())
                        .build())
                .toList();

        FamilyBackgroundResponse familyResponse = familyBackgroundRepository
                .findByProfileId(profileId)
                .map(f -> FamilyBackgroundResponse.builder()
                        .fatherName(f.getFatherName()).fatherOccupation(f.getFatherOccupation())
                        .fatherContact(f.getFatherContact()).motherName(f.getMotherName())
                        .motherOccupation(f.getMotherOccupation())
                        .motherContact(f.getMotherContact()).siblings(siblings)
                        .build())
                .orElse(null);

        // Influential Links
        List<InfluentialLinkResponse> links = influentialLinkRepository
                .findByProfileId(profileId).stream()
                .map(l -> InfluentialLinkResponse.builder()
                        .id(l.getId()).personName(l.getPersonName())
                        .profileDetails(l.getProfileDetails()).relationship(l.getRelationship())
                        .build())
                .toList();

        // Current Status
        CurrentStatusResponse statusResponse = currentStatusRepository
                .findByProfileId(profileId)
                .map(s -> CurrentStatusResponse.builder()
                        .status(s.getStatus()).lastKnownLocation(s.getLastKnownLocation())
                        .statusDate(s.getStatusDate()).remarks(s.getRemarks())
                        .build())
                .orElse(null);

        // Additional Info
        AdditionalInfoResponse additionalResponse = additionalInfoRepository
                .findByProfileId(profileId)
                .map(a -> AdditionalInfoResponse.builder()
                        .notes(a.getNotes()).behavioralNotes(a.getBehavioralNotes())
                        .riskLevel(a.getRiskLevel()).tags(a.getTags())
                        .additionalPhotos(a.getAdditionalPhotos())
                        .attachments(a.getAttachments()).linkedCases(a.getLinkedCases())
                        .build())
                .orElse(null);

        // Step Statuses
        List<StepStatusResponse> stepStatuses = stepStatusRepository
                .findByProfileIdOrderByStepNumber(profileId).stream()
                .map(s -> StepStatusResponse.builder()
                        .stepNumber(s.getStepNumber()).stepName(s.getStepName())
                        .status(s.getStatus()).updatedAt(s.getUpdatedAt())
                        .build())
                .toList();

        return ProfileDetailResponse.builder()
                .id(profile.getId())
                .profileNumber(profile.getProfileNumber())
                .name(profile.getName())
                .status(profile.getStatus())
                .createdBy(profile.getCreatedBy())
                .updatedBy(profile.getUpdatedBy())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .personalInfo(personalInfoResponse)
                .physicalAttributes(physicalResponse)
                .address(addressResponse)
                .contactInfo(contactResponse)
                .identificationDocs(idDocsResponse)
                .businessActivities(businessResponse)
                .geographicExposure(geoResponse)
                .associatedCompanies(companies)
                .firs(firs)
                .materialSeized(materialSeized)
                .vehicles(vehicles)
                .knownAssociates(knownAssociates)
                .knownEmployees(knownEmployees)
                .productsOperations(opsResponse)
                .productsInfringed(productsInfringed)
                .familyBackground(familyResponse)
                .influentialLinks(links)
                .currentStatus(statusResponse)
                .additionalInfo(additionalResponse)
                .stepStatuses(stepStatuses)
                .build();
    }

    private AssociateResponse mapAssociate(OpProfileAssociate a) {
        return AssociateResponse.builder()
                .id(a.getId()).name(a.getName())
                .relationship(a.getRelationship()).role(a.getRole())
                .contactInfo(a.getContactInfo()).notes(a.getNotes())
                .build();
    }

    private PagedProfileResponse mapToPagedResponse(Page<OpProfile> page) {
        List<ProfileSummaryResponse> summaries = page.getContent().stream()
                .map(p -> {
                    long completed = stepStatusRepository
                            .countByProfileIdAndStatus(p.getId(), StepStatus.COMPLETED);
                    String photo = personalInfoRepository.findByProfileId(p.getId())
                            .map(OpProfilePersonalInfo::getProfilePhoto).orElse(null);
                    RiskLevel risk = additionalInfoRepository.findByProfileId(p.getId())
                            .map(OpProfileAdditionalInfo::getRiskLevel).orElse(null);
                    return ProfileSummaryResponse.builder()
                            .id(p.getId())
                            .profileNumber(p.getProfileNumber())
                            .name(p.getName())
                            .profilePhoto(photo)
                            .status(p.getStatus())
                            .riskLevel(risk)
                            .createdBy(p.getCreatedBy())
                            .updatedBy(p.getUpdatedBy())
                            .createdAt(p.getCreatedAt())
                            .updatedAt(p.getUpdatedAt())
                            .completedSteps(completed)
                            .totalSteps(TOTAL_STEPS)
                            .build();
                })
                .toList();

        return PagedProfileResponse.builder()
                .profiles(summaries)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
