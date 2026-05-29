package com.tbcpl.workforce.admin.controller;

import com.tbcpl.workforce.admin.dto.ClientRequestDTO;
import com.tbcpl.workforce.admin.dto.ClientResponseDTO;
import com.tbcpl.workforce.admin.service.ClientService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping({
        ApiEndpoints.ADMIN_BASE + ApiEndpoints.CLIENTS,
        ApiEndpoints.ADMIN_BASE
})
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    @PostMapping(ApiEndpoints.CLIENTS)
    public ResponseEntity<ApiResponse<ClientResponseDTO>> createClient(
            @Valid @RequestBody ClientRequestDTO requestDTO) {
        log.info("POST /api/v1/admin/clients - Create client request");
        ClientResponseDTO response = clientService.createClient(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Client created successfully", response));
    }

    @PostMapping(ApiEndpoints.CLIENT_LOGO)
    public ResponseEntity<ApiResponse<ClientResponseDTO>> uploadLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        log.info("POST /api/v1/admin/clients/{}/logo - Upload logo", id);
        ClientResponseDTO response = clientService.uploadClientLogo(id, file);
        return ResponseEntity.ok(ApiResponse.success("Logo uploaded successfully", response));
    }

    @GetMapping(ApiEndpoints.CLIENT_LOGO)
    public ResponseEntity<byte[]> getClientLogo(@PathVariable Long id) {
        log.info("GET /api/v1/admin/clients/{}/logo - Download logo", id);
        byte[] logo = clientService.getClientLogo(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(logo.length);
        headers.setContentDispositionFormData("attachment", "client-logo-" + id + ".jpg");

        return ResponseEntity.ok().headers(headers).body(logo);
    }

    @GetMapping(ApiEndpoints.CLIENTS)
    public ResponseEntity<ApiResponse<List<ClientResponseDTO>>> getAllClients() {
        log.info("GET /api/v1/admin/clients - Get all clients");
        List<ClientResponseDTO> response = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success("Clients retrieved successfully", response));
    }

    @GetMapping(ApiEndpoints.ADMIN_DROPDOWN_CLIENTS)
    public ResponseEntity<ApiResponse<List<ClientResponseDTO>>> getClientDropdown() {
        log.info("GET /api/v1/admin/dropdowns/clients - Get clients dropdown");
        List<ClientResponseDTO> response = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success("Clients retrieved successfully", response));
    }

    @GetMapping(ApiEndpoints.CLIENT_BY_ID)
    public ResponseEntity<ApiResponse<ClientResponseDTO>> getClientById(@PathVariable Long id) {
        log.info("GET /api/v1/admin/clients/{} - Get client by ID", id);
        ClientResponseDTO response = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success("Client retrieved successfully", response));
    }

    @PutMapping(ApiEndpoints.CLIENT_BY_ID)
    public ResponseEntity<ApiResponse<ClientResponseDTO>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO requestDTO) {
        log.info("PUT /api/v1/admin/clients/{} - Update client", id);
        ClientResponseDTO response = clientService.updateClient(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Client updated successfully", response));
    }

    @DeleteMapping(ApiEndpoints.CLIENT_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        log.info("DELETE /api/v1/admin/clients/{} - Delete client", id);
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success("Client deleted successfully"));
    }

    @DeleteMapping(ApiEndpoints.CLIENT_LOGO)
    public ResponseEntity<ApiResponse<ClientResponseDTO>> deleteClientLogo(@PathVariable Long id) {
        log.info("DELETE /api/v1/admin/clients/{}/logo - Delete logo", id);
        ClientResponseDTO response = clientService.deleteClientLogo(id);
        return ResponseEntity.ok(ApiResponse.success("Logo deleted successfully", response));
    }
}