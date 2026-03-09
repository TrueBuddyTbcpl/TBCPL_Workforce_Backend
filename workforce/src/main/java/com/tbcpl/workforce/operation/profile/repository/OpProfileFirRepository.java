package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileFir;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileFirRepository extends JpaRepository<OpProfileFir, Long> {
    List<OpProfileFir> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}
