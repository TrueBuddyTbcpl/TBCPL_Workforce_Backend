package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileAssociate;
import com.tbcpl.workforce.operation.profile.enums.AssociateRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileAssociateRepository extends JpaRepository<OpProfileAssociate, Long> {
    List<OpProfileAssociate> findByProfileId(Long profileId);
    List<OpProfileAssociate> findByProfileIdAndRole(Long profileId, AssociateRole role);
    void deleteByProfileId(Long profileId);
}
