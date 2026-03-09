package com.tbcpl.workforce.operation.cases.repository;

import com.tbcpl.workforce.operation.cases.entity.CaseLinkedProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseLinkedProfileRepository extends JpaRepository<CaseLinkedProfile, Long> {

    List<CaseLinkedProfile> findByCaseEntity_Id(Long caseId);

    boolean existsByCaseEntity_IdAndProfileId(Long caseId, Long profileId);

    void deleteByCaseEntity_IdAndProfileId(Long caseId, Long profileId);

    // For profile detail — how many cases linked this profile
    @Query("SELECT COUNT(c) FROM CaseLinkedProfile c WHERE c.profileId = :profileId")
    long countByProfileId(Long profileId);

    @Query("SELECT c FROM CaseLinkedProfile c WHERE c.profileId = :profileId")
    List<CaseLinkedProfile> findByProfileId(Long profileId);
}
