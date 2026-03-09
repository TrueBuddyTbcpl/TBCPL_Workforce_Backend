package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileSibling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileSiblingRepository extends JpaRepository<OpProfileSibling, Long> {
    List<OpProfileSibling> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}
