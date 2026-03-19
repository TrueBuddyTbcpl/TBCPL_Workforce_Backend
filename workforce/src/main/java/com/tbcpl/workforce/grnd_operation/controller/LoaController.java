package com.tbcpl.workforce.grnd_operation.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.grnd_operation.dto.request.LoaRequestDto;
import com.tbcpl.workforce.grnd_operation.dto.response.ClientDropdownDto;
import com.tbcpl.workforce.grnd_operation.dto.response.EmployeeDropdownDto;
import com.tbcpl.workforce.grnd_operation.dto.response.LoaAssetsResponseDto;
import com.tbcpl.workforce.grnd_operation.dto.response.LoaResponseDto;
import com.tbcpl.workforce.grnd_operation.service.LoaAssetsService;
import com.tbcpl.workforce.grnd_operation.service.LoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grnd-operation/loa")
@RequiredArgsConstructor
@Slf4j
public class LoaController {

    private final LoaService       loaService;
    private final LoaAssetsService loaAssetsService;

    // ═══════════════════════════════════════════════════════════════════
    // LOA ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/dropdown/employees")
    public ResponseEntity<ApiResponse<List<EmployeeDropdownDto>>> getEmployeeDropdown() {
        return ResponseEntity.ok(
                ApiResponse.success("Field Associate employees fetched successfully.",
                        loaService.getFieldAssociateDropdown()));
    }

    @GetMapping("/dropdown/clients")
    public ResponseEntity<ApiResponse<List<ClientDropdownDto>>> getClientDropdown() {
        return ResponseEntity.ok(
                ApiResponse.success("Clients fetched successfully.",
                        loaService.getClientDropdown()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoaResponseDto>> createLoa(
            @Valid @RequestBody LoaRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("LOA created successfully.",
                        loaService.createLoa(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LoaResponseDto>> updateLoa(
            @PathVariable Long id,
            @Valid @RequestBody LoaRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.success("LOA updated successfully.",
                        loaService.updateLoa(id, request)));
    }

    @PutMapping("/{id}/finalize")
    public ResponseEntity<ApiResponse<LoaResponseDto>> finalizeLoa(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("LOA finalized successfully.",
                        loaService.finalizeLoa(id)));
    }

    @PostMapping("/{id}/send-mail")
    public ResponseEntity<ApiResponse<Void>> sendMail(@PathVariable Long id) {
        loaService.sendLoaByMail(id);
        return ResponseEntity.ok(
                ApiResponse.success("Authority Letter sent to employee email successfully.", null));
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> previewPdf(@PathVariable Long id) {
        byte[] pdfBytes = loaService.previewLoaPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename("authority-letter.pdf")
                                .build()
                                .toString())
                .body(pdfBytes);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LoaResponseDto>>> getAllLoas(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("LOAs fetched successfully.",
                        loaService.getAllLoas(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoaResponseDto>> getLoaById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("LOA fetched successfully.",
                        loaService.getLoaById(id)));
    }

    // ═══════════════════════════════════════════════════════════════════
    // ASSETS ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/assets")
    public ResponseEntity<ApiResponse<LoaAssetsResponseDto>> getAssets() {
        return ResponseEntity.ok(
                ApiResponse.success("Assets fetched successfully.",
                        loaAssetsService.getAssets()));
    }

    @PostMapping(value = "/assets/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LoaAssetsResponseDto>> uploadLogo(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ApiResponse.success("Logo uploaded successfully.",
                        loaAssetsService.uploadLogo(file)));
    }

    @PostMapping(value = "/assets/stamp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LoaAssetsResponseDto>> uploadStamp(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ApiResponse.success("Stamp uploaded successfully.",
                        loaAssetsService.uploadStamp(file)));
    }

    @PostMapping(value = "/assets/signature", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LoaAssetsResponseDto>> uploadSignature(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                ApiResponse.success("Signature uploaded successfully.",
                        loaAssetsService.uploadSignature(file)));
    }

    @DeleteMapping("/assets/logo")
    public ResponseEntity<ApiResponse<LoaAssetsResponseDto>> deleteLogo() {
        return ResponseEntity.ok(
                ApiResponse.success("Logo removed.", loaAssetsService.deleteLogo()));
    }

    @DeleteMapping("/assets/stamp")
    public ResponseEntity<ApiResponse<LoaAssetsResponseDto>> deleteStamp() {
        return ResponseEntity.ok(
                ApiResponse.success("Stamp removed.", loaAssetsService.deleteStamp()));
    }

    @DeleteMapping("/assets/signature")
    public ResponseEntity<ApiResponse<LoaAssetsResponseDto>> deleteSignature() {
        return ResponseEntity.ok(
                ApiResponse.success("Signature removed.", loaAssetsService.deleteSignature()));
    }
}
