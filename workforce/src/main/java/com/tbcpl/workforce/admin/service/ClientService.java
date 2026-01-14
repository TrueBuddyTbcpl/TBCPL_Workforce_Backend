package com.tbcpl.workforce.admin.service;

import com.tbcpl.workforce.admin.dto.ClientRequestDTO;
import com.tbcpl.workforce.admin.dto.ClientResponseDTO;
import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.InvalidFileException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
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
        log.info("Creating new client: {}", requestDTO.getClientName());

        if (clientRepository.existsByClientNameAndDeletedFalse(requestDTO.getClientName())) {
            throw new DuplicateResourceException("Client", "name", requestDTO.getClientName());
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
        log.info("Uploading logo for client ID: {}", clientId);

        Client client = clientRepository.findActiveClientById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileException("Only image files are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidFileException("File size exceeds 5MB limit");
        }

        client.setClientLogo(file.getBytes());
        client.setLogoFileName(file.getOriginalFilename());
        client.setLogoContentType(file.getContentType());

        Client updatedClient = clientRepository.save(client);
        log.info("Logo uploaded successfully for client ID: {}", clientId);

        return mapToResponseDTO(updatedClient);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getAllClients() {
        log.info("Fetching all active clients");
        return clientRepository.findAllActiveClients().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Long id) {
        log.info("Fetching client with ID: {}", id);
        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        return mapToResponseDTO(client);
    }

    @Transactional(readOnly = true)
    public byte[] getClientLogo(Long id) {
        log.info("Fetching logo for client ID: {}", id);
        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        if (client.getClientLogo() == null) {
            throw new ResourceNotFoundException("Client logo not found for client with id: " + id);
        }

        return client.getClientLogo();
    }

    public ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO) {
        log.info("Updating client with ID: {}", id);

        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        if (!client.getClientName().equals(requestDTO.getClientName()) &&
                clientRepository.existsByClientNameAndDeletedFalse(requestDTO.getClientName())) {
            throw new DuplicateResourceException("Client", "name", requestDTO.getClientName());
        }

        client.setClientName(requestDTO.getClientName());
        Client updatedClient = clientRepository.save(client);

        log.info("Client updated successfully with ID: {}", id);
        return mapToResponseDTO(updatedClient);
    }

    public void deleteClient(Long id) {
        log.info("Soft deleting client with ID: {}", id);

        Client client = clientRepository.findActiveClientById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

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
