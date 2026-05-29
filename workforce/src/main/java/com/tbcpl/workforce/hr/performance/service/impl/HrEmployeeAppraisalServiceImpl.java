package com.tbcpl.workforce.hr.performance.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.performance.dto.request.*;
import com.tbcpl.workforce.hr.performance.dto.response.*;
import com.tbcpl.workforce.hr.performance.entity.*;
import com.tbcpl.workforce.hr.performance.entity.enums.*;
import com.tbcpl.workforce.hr.performance.repository.*;
import com.tbcpl.workforce.hr.performance.service.HrEmployeeAppraisalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrEmployeeAppraisalServiceImpl implements HrEmployeeAppraisalService {

    private final HrEmployeeAppraisalRepository appraisalRepository;
    private final HrAppraisalCycleRepository    cycleRepository;
    private final HrKraTemplateRepository       kraTemplateRepository;
    private final HrKraRatingRepository         kraRatingRepository;
    private final EmployeeRepository            employeeRepository;
    private final EmployeeNameResolverService    nameResolver;

    @Override
    @Transactional
    public HrEmployeeAppraisalResponse initiateAppraisal(String empId, Long cycleId,
                                                         String managerEmpId,
                                                         String createdBy) {
        log.info("Initiating appraisal for empId:{} cycleId:{}", empId, cycleId);

        validateEmployeeExists(empId);
        HrAppraisalCycle cycle = findCycleById(cycleId);

        if (appraisalRepository.existsByEmpIdAndAppraisalCycleIdAndIsActiveTrue(
                empId, cycleId)) {
            throw new DuplicateResourceException(
                    "Appraisal already exists for empId: " + empId
                            + " in cycle ID: " + cycleId);
        }

        HrEmployeeAppraisal appraisal = HrEmployeeAppraisal.builder()
                .empId(empId.trim())
                .appraisalCycle(cycle)
                .managerEmpId(managerEmpId)
                .status(AppraisalStatus.SELF_REVIEW_PENDING)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrEmployeeAppraisal saved = appraisalRepository.save(appraisal);
        log.info("Appraisal initiated ID:{} for empId:{}", saved.getId(), empId);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public int bulkInitiateAppraisals(Long cycleId, List<String> empIds,
                                      String managerEmpId, String createdBy) {
        log.info("Bulk initiating appraisals for cycleId:{} empCount:{}",
                cycleId, empIds.size());
        HrAppraisalCycle cycle = findCycleById(cycleId);
        int created = 0;

        for (String empId : empIds) {
            if (!employeeRepository.existsByEmpId(empId)) {
                log.warn("Skipping empId:{} — not found", empId);
                continue;
            }
            if (appraisalRepository.existsByEmpIdAndAppraisalCycleIdAndIsActiveTrue(
                    empId, cycleId)) {
                log.warn("Skipping empId:{} — appraisal already exists in cycle:{}", empId, cycleId);
                continue;
            }
            HrEmployeeAppraisal appraisal = HrEmployeeAppraisal.builder()
                    .empId(empId.trim())
                    .appraisalCycle(cycle)
                    .managerEmpId(managerEmpId)
                    .status(AppraisalStatus.SELF_REVIEW_PENDING)
                    .isActive(true)
                    .createdBy(createdBy)
                    .build();
            appraisalRepository.save(appraisal);
            created++;
        }
        log.info("Bulk initiation complete. Created:{} skipped:{}",
                created, empIds.size() - created);
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public HrEmployeeAppraisalResponse getAppraisalById(Long id) {
        HrEmployeeAppraisal appraisal = appraisalRepository
                .findByIdWithKraRatings(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appraisal not found with ID: " + id));
        return mapToResponse(appraisal, resolveCreatedBy(appraisal.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrEmployeeAppraisalResponse getAppraisalByEmpAndCycle(String empId, Long cycleId) {
        HrEmployeeAppraisal appraisal = appraisalRepository
                .findByEmpIdAndAppraisalCycleIdAndIsActiveTrue(empId, cycleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No appraisal found for empId: " + empId + " cycleId: " + cycleId));
        return mapToResponse(appraisal, resolveCreatedBy(appraisal.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeAppraisalResponse> getAppraisalsByCycle(Long cycleId,
                                                                  int page, int size) {
        return appraisalRepository
                .findByAppraisalCycleIdAndIsActiveTrueOrderByEmpIdAsc(
                        cycleId, PageRequest.of(page, size))
                .map(a -> mapToResponse(a, resolveCreatedBy(a.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeAppraisalResponse> getAppraisalsByEmpId(String empId,
                                                                  int page, int size) {
        return appraisalRepository
                .findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
                        empId, PageRequest.of(page, size,
                                Sort.by("createdAt").descending()))
                .map(a -> mapToResponse(a, resolveCreatedBy(a.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeAppraisalResponse> getAppraisalsByStatus(String status,
                                                                   int page, int size) {
        AppraisalStatus appraisalStatus = AppraisalStatus.valueOf(status.toUpperCase());
        return appraisalRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        appraisalStatus, PageRequest.of(page, size))
                .map(a -> mapToResponse(a, resolveCreatedBy(a.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrEmployeeAppraisalResponse submitSelfReview(Long id,
                                                        HrSelfReviewRequest request,
                                                        String empId) {
        log.info("Self review submission for appraisal ID:{} empId:{}", id, empId);
        HrEmployeeAppraisal appraisal = findAppraisalById(id);

        if (!appraisal.getEmpId().equals(empId)) {
            throw new IllegalStateException(
                    "Employee is not authorized to submit review for this appraisal");
        }
        if (appraisal.getStatus() != AppraisalStatus.SELF_REVIEW_PENDING) {
            throw new IllegalStateException(
                    "Self review can only be submitted when status is SELF_REVIEW_PENDING. "
                            + "Current: " + appraisal.getStatus());
        }

        appraisal.setSelfReviewComments(request.getSelfReviewComments());
        appraisal.setSelfRating(request.getSelfRating());
        appraisal.setSelfReviewSubmittedAt(LocalDateTime.now());
        appraisal.setStatus(AppraisalStatus.SELF_REVIEW_DONE);

        // Persist KRA self ratings
        persistKraRatings(appraisal, request.getKraRatings(), true, empId);

        HrEmployeeAppraisal saved = appraisalRepository.save(appraisal);
        log.info("Self review submitted for appraisal ID:{}", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrEmployeeAppraisalResponse submitManagerReview(Long id,
                                                           HrManagerReviewRequest request,
                                                           String managerEmpId) {
        log.info("Manager review submission for appraisal ID:{} managerEmpId:{}",
                id, managerEmpId);
        HrEmployeeAppraisal appraisal = findAppraisalById(id);

        if (appraisal.getStatus() != AppraisalStatus.SELF_REVIEW_DONE
                && appraisal.getStatus() != AppraisalStatus.MANAGER_REVIEW_PENDING) {
            throw new IllegalStateException(
                    "Manager review can only be submitted after self review is done. "
                            + "Current: " + appraisal.getStatus());
        }

        appraisal.setManagerReviewComments(request.getManagerReviewComments());
        appraisal.setManagerRating(request.getManagerRating());
        appraisal.setManagerReviewSubmittedAt(LocalDateTime.now());
        appraisal.setStatus(AppraisalStatus.MANAGER_REVIEW_DONE);

        // Persist manager KRA ratings and compute weighted score
        persistKraRatings(appraisal, request.getKraRatings(), false, managerEmpId);
        double weightedScore = computeWeightedScore(appraisal);
        appraisal.setFinalScore(Math.round(weightedScore * 100.0) / 100.0);

        HrEmployeeAppraisal saved = appraisalRepository.save(appraisal);
        log.info("Manager review submitted for appraisal ID:{} score:{}",
                id, appraisal.getFinalScore());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrEmployeeAppraisalResponse submitFinalReview(Long id,
                                                         HrFinalReviewRequest request,
                                                         String hrEmpId) {
        log.info("HR final review for appraisal ID:{} by:{}", id, hrEmpId);
        HrEmployeeAppraisal appraisal = findAppraisalById(id);

        if (appraisal.getStatus() != AppraisalStatus.MANAGER_REVIEW_DONE
                && appraisal.getStatus() != AppraisalStatus.HR_REVIEW_PENDING) {
            throw new IllegalStateException(
                    "Final review can only be submitted after manager review is done. "
                            + "Current: " + appraisal.getStatus());
        }

        appraisal.setHrReviewComments(request.getHrReviewComments());
        appraisal.setFinalRating(request.getFinalRating());
        appraisal.setIncrementPercentage(request.getIncrementPercentage());
        appraisal.setHrReviewSubmittedAt(LocalDateTime.now());
        appraisal.setStatus(AppraisalStatus.COMPLETED);
        appraisal.setCreatedBy(hrEmpId);

        HrEmployeeAppraisal saved = appraisalRepository.save(appraisal);
        log.info("Final review completed for appraisal ID:{} finalRating:{} increment:{}%",
                id, request.getFinalRating(), request.getIncrementPercentage());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteAppraisal(Long id) {
        log.info("Soft deleting appraisal ID:{}", id);
        HrEmployeeAppraisal appraisal = findAppraisalById(id);
        appraisal.setIsActive(false);
        appraisalRepository.save(appraisal);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Upsert KRA ratings for self or manager review.
     * For manager review: computes and sets weightedScore per KRA.
     */
    private void persistKraRatings(HrEmployeeAppraisal appraisal,
                                   List<HrKraRatingRequest> ratingRequests,
                                   boolean isSelfReview,
                                   String actor) {
        Map<Long, HrKraRating> existingByKraId = appraisal.getKraRatings().stream()
                .collect(Collectors.toMap(
                        r -> r.getKraTemplate().getId(),
                        r -> r,
                        (a, b) -> a
                ));

        for (HrKraRatingRequest req : ratingRequests) {
            HrKraTemplate template = kraTemplateRepository.findById(req.getKraTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "KRA template not found: " + req.getKraTemplateId()));

            HrKraRating rating = existingByKraId.getOrDefault(
                    req.getKraTemplateId(),
                    HrKraRating.builder()
                            .employeeAppraisal(appraisal)
                            .kraTemplate(template)
                            .isActive(true)
                            .createdBy(actor)
                            .build()
            );

            if (isSelfReview) {
                rating.setAchievedValue(req.getAchievedValue());
                rating.setSelfRating(req.getSelfRating());
                rating.setSelfComments(req.getSelfComments());
            } else {
                rating.setManagerRating(req.getManagerRating());
                rating.setManagerComments(req.getManagerComments());
                // Compute weighted score: ratingNumeric * weightage / 100
                if (req.getManagerRating() != null) {
                    double numericRating = toNumeric(req.getManagerRating());
                    double weighted = numericRating * template.getWeightage() / 100.0;
                    rating.setWeightedScore(Math.round(weighted * 100.0) / 100.0);
                }
            }

            if (!existingByKraId.containsKey(req.getKraTemplateId())) {
                appraisal.getKraRatings().add(rating);
            }
        }
    }

    /**
     * Sums all KRA weighted scores for the appraisal.
     * Total = Sum(managerRating_numeric × KRA_weightage / 100)
     */
    private double computeWeightedScore(HrEmployeeAppraisal appraisal) {
        return appraisal.getKraRatings().stream()
                .filter(r -> r.getWeightedScore() != null)
                .mapToDouble(HrKraRating::getWeightedScore)
                .sum();
    }

    /**
     * Maps RatingScale enum to numeric value (1–5) for score computation.
     */
    private double toNumeric(RatingScale rating) {
        return switch (rating) {
            case OUTSTANDING          -> 5.0;
            case EXCEEDS_EXPECTATIONS -> 4.0;
            case MEETS_EXPECTATIONS   -> 3.0;
            case NEEDS_IMPROVEMENT    -> 2.0;
            case UNSATISFACTORY       -> 1.0;
        };
    }

    private HrEmployeeAppraisal findAppraisalById(Long id) {
        return appraisalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appraisal not found with ID: " + id));
    }

    private HrAppraisalCycle findCycleById(Long id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appraisal cycle not found with ID: " + id));
    }

    private void validateEmployeeExists(String empId) {
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private HrEmployeeAppraisalResponse mapToResponse(HrEmployeeAppraisal a,
                                                      Map<String, String> nameMap) {
        String raw = a.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        HrAppraisalCycle cycle = a.getAppraisalCycle();

        List<HrKraRatingResponse> kraResponses =
                (a.getKraRatings() == null || a.getKraRatings().isEmpty())
                        ? Collections.emptyList()
                        : a.getKraRatings().stream()
                        .filter(r -> Boolean.TRUE.equals(r.getIsActive()))
                        .map(this::mapKraRatingToResponse)
                        .collect(Collectors.toList());

        return HrEmployeeAppraisalResponse.builder()
                .id(a.getId())
                .empId(a.getEmpId())
                .cycleId(cycle != null ? cycle.getId() : null)
                .cycleName(cycle != null ? cycle.getCycleName() : null)
                .cycleType(cycle != null ? cycle.getCycleType().name() : null)
                .managerEmpId(a.getManagerEmpId())
                .selfReviewComments(a.getSelfReviewComments())
                .selfReviewSubmittedAt(a.getSelfReviewSubmittedAt())
                .selfRating(a.getSelfRating())
                .managerReviewComments(a.getManagerReviewComments())
                .managerReviewSubmittedAt(a.getManagerReviewSubmittedAt())
                .managerRating(a.getManagerRating())
                .hrReviewComments(a.getHrReviewComments())
                .hrReviewSubmittedAt(a.getHrReviewSubmittedAt())
                .finalRating(a.getFinalRating())
                .finalScore(a.getFinalScore())
                .incrementPercentage(a.getIncrementPercentage())
                .status(a.getStatus())
                .kraRatings(kraResponses)
                .isActive(a.getIsActive())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }

    private HrKraRatingResponse mapKraRatingToResponse(HrKraRating r) {
        HrKraTemplate t = r.getKraTemplate();
        return HrKraRatingResponse.builder()
                .id(r.getId())
                .kraTemplateId(t != null ? t.getId() : null)
                .kraName(t != null ? t.getKraName() : null)
                .weightage(t != null ? t.getWeightage() : null)
                .targetValue(t != null ? t.getTargetValue() : null)
                .measurementUnit(t != null ? t.getMeasurementUnit() : null)
                .achievedValue(r.getAchievedValue())
                .selfRating(r.getSelfRating())
                .selfComments(r.getSelfComments())
                .managerRating(r.getManagerRating())
                .managerComments(r.getManagerComments())
                .weightedScore(r.getWeightedScore())
                .build();
    }
}