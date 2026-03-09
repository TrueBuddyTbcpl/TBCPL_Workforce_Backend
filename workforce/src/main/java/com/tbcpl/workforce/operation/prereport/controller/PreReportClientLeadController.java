package com.tbcpl.workforce.operation.prereport.controller;

import com.tbcpl.workforce.operation.prereport.dto.request.*;
import com.tbcpl.workforce.operation.prereport.dto.response.ClientLeadStepResponse;
import com.tbcpl.workforce.operation.prereport.service.PreReportClientLeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operation/prereport")
@Slf4j
public class PreReportClientLeadController {

    private final PreReportClientLeadService clientLeadService;

    public PreReportClientLeadController(PreReportClientLeadService clientLeadService) {
        this.clientLeadService = clientLeadService;
    }

    @GetMapping("/{prereportId}/client-lead")
    public ResponseEntity<ClientLeadStepResponse> getClientLeadData(@PathVariable Long prereportId) {
        log.info("GET /api/v1/operation/prereport/{}/client-lead - Fetching client lead data", prereportId);

        ClientLeadStepResponse response = clientLeadService.getClientLeadByPrereportId(prereportId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/1")
    public ResponseEntity<ClientLeadStepResponse> updateStep1(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep1Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/1 - Updating step 1", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep1(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/2")
    public ResponseEntity<ClientLeadStepResponse> updateStep2(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep2Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/2 - Updating step 2", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep2(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/3")
    public ResponseEntity<ClientLeadStepResponse> updateStep3(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep3Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/3 - Updating step 3", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep3(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/4")
    public ResponseEntity<ClientLeadStepResponse> updateStep4(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep4Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/4 - Updating step 4", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep4(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/5")
    public ResponseEntity<ClientLeadStepResponse> updateStep5(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep5Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/5 - Updating step 5", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep5(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/6")
    public ResponseEntity<ClientLeadStepResponse> updateStep6(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep6Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/6 - Updating step 6", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep6(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/7")
    public ResponseEntity<ClientLeadStepResponse> updateStep7(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep7Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/7 - Updating step 7", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep7(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/8")
    public ResponseEntity<ClientLeadStepResponse> updateStep8(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep8Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/8 - Updating step 8", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep8(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/9")
    public ResponseEntity<ClientLeadStepResponse> updateStep9(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep9Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/9 - Updating step 9", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep9(prereportId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prereportId}/client-lead/step/10")
    public ResponseEntity<ClientLeadStepResponse> updateStep10(
            @PathVariable Long prereportId,
            @RequestBody ClientLeadStep10Request request) {
        log.info("PUT /api/v1/operation/prereport/{}/client-lead/step/10 - Updating step 10", prereportId);

        ClientLeadStepResponse response = clientLeadService.updateStep10(prereportId, request);
        return ResponseEntity.ok(response);
    }
}
