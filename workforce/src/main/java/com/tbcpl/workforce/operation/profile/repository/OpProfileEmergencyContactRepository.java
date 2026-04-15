package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileEmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileEmergencyContactRepository extends JpaRepository<OpProfileEmergencyContact, Long> {
    List<OpProfileEmergencyContact> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}