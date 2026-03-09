package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileMaterialSeized;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileMaterialSeizedRepository extends JpaRepository<OpProfileMaterialSeized, Long> {
    List<OpProfileMaterialSeized> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}
