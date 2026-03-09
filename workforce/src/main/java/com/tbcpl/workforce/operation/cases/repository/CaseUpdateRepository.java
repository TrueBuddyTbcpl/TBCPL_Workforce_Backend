package com.tbcpl.workforce.operation.cases.repository;

import com.tbcpl.workforce.operation.cases.entity.CaseUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseUpdateRepository extends JpaRepository<CaseUpdate, Long> {

    List<CaseUpdate> findByCaseEntity_IdOrderByUpdateDateDesc(Long caseId);
}
