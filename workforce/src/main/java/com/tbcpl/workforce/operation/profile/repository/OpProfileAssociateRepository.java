package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileAssociate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileAssociateRepository extends JpaRepository<OpProfileAssociate, Long> {

    List<OpProfileAssociate> findByProfileId(Long profileId);

    // ── Was: findByProfileIdAndRole(Long, AssociateRole) ─────────────────────
    List<OpProfileAssociate> findByProfileIdAndRole(Long profileId, String role);
    // ────────────────────────────────────────────────────────────────────────

    void deleteByProfileId(Long profileId);
}