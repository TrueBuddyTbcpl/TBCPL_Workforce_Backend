package com.tbcpl.workforce.operation.cases.repository;

import com.tbcpl.workforce.operation.cases.entity.CaseOnlinePresence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseOnlinePresenceRepository extends JpaRepository<CaseOnlinePresence, Long> {

    List<CaseOnlinePresence> findByCaseEntity_Id(Long caseId);
}
