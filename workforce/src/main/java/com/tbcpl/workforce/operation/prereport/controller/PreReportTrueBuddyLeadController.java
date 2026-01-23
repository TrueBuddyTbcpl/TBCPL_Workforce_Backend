package com.tbcpl.workforce.operation.prereport.controller;

import com.tbcpl.workforce.operation.prereport.dto.request.*;
import com.tbcpl.workforce.operation.prereport.dto.response.TrueBuddyLeadStepResponse;
import com.tbcpl.workforce.operation.prereport.service.PreReportTrueBuddyLeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operation/prereport")
@Slf4j
public class PreReportTrueBuddyLeadController {

    private final PreReportTrueBuddyLeadService trueBuddyLeadService;

    public PreReportTrueBuddyLeadController(PreReportTrueBuddyLeadService trueBuddyLeadService) {
        this.trueBuddyLeadService = trueBuddyLeadService;
    }

    @GetMapping("/{prereportId}/truebuddy-lead")
    public ResponseEntity<TrueBuddyLeadStepResponse> getTrueBuddyLeadData(@PathVariable Long prereportId) {
        log.info("GET /api/v1/operation/prereport/{}/truebuddy-lead - Fetching TrueBuddy lead data", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.getTrueBuddyLeadByPrereportId(prereportId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/1")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep1(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep1Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/1 - Updating step 1", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep1(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/2")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep2(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep2Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/2 - Updating step 2", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep2(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/3")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep3(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep3Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/3 - Updating step 3", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep3(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/4")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep4(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep4Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/4 - Updating step 4", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep4(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/5")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep5(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep5Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/5 - Updating step 5", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep5(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/6")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep6(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep6Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/6 - Updating step 6", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep6(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/7")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep7(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep7Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/7 - Updating step 7", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep7(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/8")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep8(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep8Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/8 - Updating step 8", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep8(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/9")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep9(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep9Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/9 - Updating step 9", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep9(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/10")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep10(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep10Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/10 - Updating step 10", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep10(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/truebuddy-lead/step/11")
    public ResponseEntity<TrueBuddyLeadStepResponse> updateStep11(
            @PathVariable Long prereportId,
            @RequestBody TrueBuddyLeadStep11Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/truebuddy-lead/step/11 - Updating step 11", prereportId);

        TrueBuddyLeadStepResponse response = trueBuddyLeadService.updateStep11(prereportId, request);
        return ResponseEntity.ok(response);
    }
}
