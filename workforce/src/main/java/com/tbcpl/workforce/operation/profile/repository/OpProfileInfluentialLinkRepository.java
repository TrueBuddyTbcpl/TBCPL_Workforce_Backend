package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileInfluentialLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileInfluentialLinkRepository extends JpaRepository<OpProfileInfluentialLink, Long> {
    List<OpProfileInfluentialLink> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}
