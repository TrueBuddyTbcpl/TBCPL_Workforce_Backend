package com.tbcpl.workforce.admin.service;

import com.tbcpl.workforce.admin.dto.ClientProductRequestDTO;
import com.tbcpl.workforce.admin.dto.ClientProductResponseDTO;
import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.entity.ClientProduct;
import com.tbcpl.workforce.admin.repository.ClientProductRepository;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientProductService {

    private final ClientProductRepository clientProductRepository;
    private final ClientRepository clientRepository;

    public ClientProductResponseDTO createClientProduct(ClientProductRequestDTO requestDTO) {
        log.info("Creating new product: {} for client ID: {}", requestDTO.getProductName(), requestDTO.getClientId());

        Client client = clientRepository.findActiveClientById(requestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", requestDTO.getClientId()));

        ClientProduct product = new ClientProduct();
        product.setProductName(requestDTO.getProductName());
        product.setClient(client);
        product.setCreatedBy(requestDTO.getCreatedBy());
        product.setDeleted(false);

        ClientProduct savedProduct = clientProductRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return mapToResponseDTO(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ClientProductResponseDTO> getAllProducts() {
        log.info("Fetching all active products");
        return clientProductRepository.findAllActiveProducts().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientProductResponseDTO getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        ClientProduct product = clientProductRepository.findActiveProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ClientProductResponseDTO> getProductsByClientId(Long clientId) {
        log.info("Fetching products for client ID: {}", clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", "id", clientId);
        }

        return clientProductRepository.findActiveProductsByClientId(clientId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ClientProductResponseDTO updateProduct(Long id, ClientProductRequestDTO requestDTO) {
        log.info("Updating product with ID: {}", id);

        ClientProduct product = clientProductRepository.findActiveProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Client client = clientRepository.findActiveClientById(requestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", requestDTO.getClientId()));

        product.setProductName(requestDTO.getProductName());
        product.setClient(client);

        ClientProduct updatedProduct = clientProductRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);

        return mapToResponseDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        log.info("Soft deleting product with ID: {}", id);

        ClientProduct product = clientProductRepository.findActiveProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setDeleted(true);
        clientProductRepository.save(product);

        log.info("Product soft deleted successfully with ID: {}", id);
    }

    private ClientProductResponseDTO mapToResponseDTO(ClientProduct product) {
        ClientProductResponseDTO dto = new ClientProductResponseDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setClientId(product.getClient().getClientId());
        dto.setClientName(product.getClient().getClientName());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setCreatedBy(product.getCreatedBy());
        return dto;
    }
}
