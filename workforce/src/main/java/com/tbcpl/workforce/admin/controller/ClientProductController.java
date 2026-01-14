package com.tbcpl.workforce.admin.controller;

import com.tbcpl.workforce.admin.dto.ClientProductRequestDTO;
import com.tbcpl.workforce.admin.dto.ClientProductResponseDTO;
import com.tbcpl.workforce.admin.service.ClientProductService;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/client-products")
@RequiredArgsConstructor
@Slf4j
public class ClientProductController {

    private final ClientProductService clientProductService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientProductResponseDTO>> createProduct(
            @Valid @RequestBody ClientProductRequestDTO requestDTO) {
        log.info("POST /api/v1/admin/client-products - Create product");
        ClientProductResponseDTO response = clientProductService.createClientProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientProductResponseDTO>>> getAllProducts() {
        log.info("GET /api/v1/admin/client-products - Get all products");
        List<ClientProductResponseDTO> response = clientProductService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientProductResponseDTO>> getProductById(@PathVariable Long id) {
        log.info("GET /api/v1/admin/client-products/{} - Get product by ID", id);
        ClientProductResponseDTO response = clientProductService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", response));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<ClientProductResponseDTO>>> getProductsByClientId(
            @PathVariable Long clientId) {
        log.info("GET /api/v1/admin/client-products/client/{} - Get products by client ID", clientId);
        List<ClientProductResponseDTO> response = clientProductService.getProductsByClientId(clientId);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ClientProductRequestDTO requestDTO) {
        log.info("PUT /api/v1/admin/client-products/{} - Update product", id);
        ClientProductResponseDTO response = clientProductService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/v1/admin/client-products/{} - Delete product", id);
        clientProductService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
}
