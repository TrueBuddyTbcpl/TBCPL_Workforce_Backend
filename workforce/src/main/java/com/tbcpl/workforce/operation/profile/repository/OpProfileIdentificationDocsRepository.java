package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileIdentificationDocs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpProfileIdentificationDocsRepository extends JpaRepository<OpProfileIdentificationDocs, Long> {
    Optional<OpProfileIdentificationDocs> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
}
