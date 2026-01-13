package com.tbcpl.workforce.admin.service;

import com.tbcpl.workforce.admin.dto.ClientRequestDTO;
import com.tbcpl.workforce.admin.dto.ClientResponseDTO;
import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientResponseDTO createClient(ClientRequestDTO requestDTO) {
        log.info("creating new client : {}", requestDTO.getClientName());

        if (clientRepository.existsByClientNameAndDeletedFalse(requestDTO.getClientName())) {
            throw new IllegalArgumentException("client name'" + requestDTO.getClientName()
            + "' already exists");
        }

        Client client = new Client();
        client.setClientName(requestDTO.getClientName());
        client.setCreatedBy(requestDTO.getCreatedBy());
        client.setDeleted(false);

        Client savedClient = clientRepository.save(client);
        log.info("Client created successfully with ID: {}", savedClient.getClientId());

        return mapToResponseDTO(savedClient);
    }

    public ClientResponseDTO uploadClientLogo(Long clientId, MultipartFile file) throws IOException {
        log.info("uploading logo client ID : {}", clientId);

        Client client = clientRepository.findActiveClientById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("client not found with ID: " + clientId));
        if (file.isEmpty()){
            throw new IllegalArgumentException("file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        client.setClientLogo(file.getBytes());
        client.setLogoFileName(file.getOriginalFilename());
        client.setLogoContentType(file.getContentType());

        Client updatedClient = clientRepository.save(client);
        log.info("Logo Uploaded successfully with ID: {}", clientId);

        return mapToResponseDTO(updatedClient);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getAllClients() {
        log.info("fetching all active clients");
        return clientRepository.findAllActiveClients().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Long id) {
        log.info("Fetching client with ID: {}", id);
        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new IllegalArgumentException("client not found with ID: " + id));
        return mapToResponseDTO(client);
    }

    @Transactional(readOnly = true)
    public byte[] getClientLogo(Long id) {
        log.info("Fetching logo for client ID: {}", id);
        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));

        if (client.getClientLogo() == null) {
            throw new IllegalArgumentException("Client logo not found for ID: " + id);
        }

        return client.getClientLogo();
    }

    public ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO) {
        log.info("Updating client with ID: {}", id);

        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));

        if (!client.getClientName().equals(requestDTO.getClientName()) &&
                clientRepository.existsByClientNameAndDeletedFalse(requestDTO.getClientName())) {
            throw new IllegalArgumentException("Client with name '" + requestDTO.getClientName() + "' already exists");
        }

        client.setClientName(requestDTO.getClientName());
        Client updatedClient = clientRepository.save(client);

        log.info("Client updated successfully with ID: {}", id);
        return mapToResponseDTO(updatedClient);
    }

    public void deleteClient(Long id) {
        log.info("Soft deleting client with ID: {}", id);

        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));

        client.setDeleted(true);
        clientRepository.save(client);

        log.info("Client soft deleted successfully with ID: {}", id);
    }

    private ClientResponseDTO mapToResponseDTO(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.setClientId(client.getClientId());
        dto.setClientName(client.getClientName());
        dto.setLogoFileName(client.getLogoFileName());
        dto.setHasLogo(client.getClientLogo() != null);
        dto.setCreatedAt(client.getCreatedAt());
        dto.setUpdatedAt(client.getUpdatedAt());
        dto.setCreatedBy(client.getCreatedBy());
        return dto;
    }
}
