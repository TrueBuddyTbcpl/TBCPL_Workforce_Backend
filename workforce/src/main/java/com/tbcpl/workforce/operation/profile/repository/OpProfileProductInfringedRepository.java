package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileProductInfringed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileProductInfringedRepository extends JpaRepository<OpProfileProductInfringed, Long> {
    List<OpProfileProductInfringed> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}
