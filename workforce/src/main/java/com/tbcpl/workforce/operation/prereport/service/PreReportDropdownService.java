package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.entity.ClientProduct;
import com.tbcpl.workforce.admin.repository.ClientProductRepository;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import com.tbcpl.workforce.operation.prereport.dto.response.ClientDropdownResponse;
import com.tbcpl.workforce.operation.prereport.dto.response.ProductDropdownResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreReportDropdownService {

    private final ClientRepository clientRepository;
    private final ClientProductRepository clientProductRepository;

    public PreReportDropdownService(ClientRepository clientRepository,
                                    ClientProductRepository clientProductRepository) {
        this.clientRepository = clientRepository;
        this.clientProductRepository = clientProductRepository;
    }

    @Transactional(readOnly = true)
    public List<ClientDropdownResponse> getAllActiveClients() {
        log.info("Fetching all active clients for dropdown");

        List<Client> clients = clientRepository.findAllActiveClients();

        return clients.stream()
                .map(client -> ClientDropdownResponse.builder()
                        .clientId(client.getClientId())
                        .clientName(client.getClientName())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDropdownResponse> getProductsByClientId(Long clientId) {
        log.info("Fetching products for client ID: {}", clientId);

        // Validate client exists
        clientRepository.findActiveClientById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        List<ClientProduct> products = clientProductRepository.findActiveProductsByClientId(clientId);

        return products.stream()
                .map(product -> ProductDropdownResponse.builder()
                        .productId(product.getId())
                        .productName(product.getProductName())
                        .clientId(product.getClient().getClientId())
                        .build())
                .collect(Collectors.toList());
    }
}
