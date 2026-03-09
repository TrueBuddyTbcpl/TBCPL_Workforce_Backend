package com.tbcpl.workforce.admin.repository;

import com.tbcpl.workforce.admin.entity.ClientProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {

    @Query("SELECT cp FROM ClientProduct cp WHERE cp.deleted = false")
    List<ClientProduct> findAllActiveProducts();

    @Query("SELECT cp FROM ClientProduct cp WHERE cp.id = :id AND cp.deleted = false")
    Optional<ClientProduct> findActiveProductById(@Param("id") Long id);

    @Query("SELECT cp FROM ClientProduct cp WHERE cp.client.clientId = :clientId AND cp.deleted = false")
    List<ClientProduct> findActiveProductsByClientId(@Param("clientId") Long clientId);
}
