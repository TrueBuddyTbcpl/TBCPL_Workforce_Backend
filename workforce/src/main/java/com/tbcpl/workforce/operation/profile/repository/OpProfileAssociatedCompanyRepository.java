package com.tbcpl.workforce.operation.profile.repository;

import com.tbcpl.workforce.operation.profile.entity.OpProfileAssociatedCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpProfileAssociatedCompanyRepository extends JpaRepository<OpProfileAssociatedCompany, Long> {
    List<OpProfileAssociatedCompany> findByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
}
