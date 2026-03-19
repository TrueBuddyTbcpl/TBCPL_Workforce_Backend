package com.tbcpl.workforce.grnd_operation.repository;

import com.tbcpl.workforce.grnd_operation.entity.LoaAssets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaAssetsRepository extends JpaRepository<LoaAssets, Long> {
    Optional<LoaAssets> findTopByOrderByIdAsc();
}
