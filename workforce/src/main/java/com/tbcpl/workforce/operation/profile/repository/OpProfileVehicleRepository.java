package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileVehicleRepository extends JpaRepository<OpProfileVehicle, Long> {
    List<OpProfileVehicle> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}
