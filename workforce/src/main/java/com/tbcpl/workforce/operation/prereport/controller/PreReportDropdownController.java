package com.tbcpl.workforce.operation.prereport.controller;

import com.tbcpl.workforce.operation.prereport.dto.response.ClientDropdownResponse;
import com.tbcpl.workforce.operation.prereport.dto.response.ProductDropdownResponse;
import com.tbcpl.workforce.operation.prereport.service.PreReportDropdownService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation/prereport/dropdown")
@Slf4j
public class PreReportDropdownController {

    private final PreReportDropdownService dropdownService;

    public PreReportDropdownController(PreReportDropdownService dropdownService) {
        this.dropdownService = dropdownService;
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientDropdownResponse>> getAllClients() {
        log.info("GET /api/v1/operation/prereport/dropdown/clients - Fetching all active clients");

        List<ClientDropdownResponse> clients = dropdownService.getAllActiveClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/products/client/{clientId}")
    public ResponseEntity<List<ProductDropdownResponse>> getProductsByClient(@PathVariable Long clientId) {
        log.info("GET /api/v1/operation/prereport/dropdown/products/client/{} - Fetching products for client", clientId);

        List<ProductDropdownResponse> products = dropdownService.getProductsByClientId(clientId);
        return ResponseEntity.ok(products);
    }
}
