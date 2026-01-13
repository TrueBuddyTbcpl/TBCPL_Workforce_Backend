package com.tbcpl.workforce.admin.repository;

import com.tbcpl.workforce.admin.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.deleted = false")
    List<Client> findAllActiveClients();

    @Query("SELECT c FROM Client c WHERE c.clientId = :id AND c.deleted = false")
    Optional<Client> findActiveClientById(Long id);

    // Use Spring Data JPA method naming convention
    boolean existsByClientNameAndDeletedFalse(String clientName);
}
