package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PreReportCustomScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreReportCustomScopeRepository extends JpaRepository<PreReportCustomScope, Long> {

    @Query("SELECT pcs FROM PreReportCustomScope pcs WHERE pcs.isActive = true ORDER BY pcs.scopeName ASC")
    List<PreReportCustomScope> findAllActiveScopes();

    Optional<PreReportCustomScope> findByScopeNameIgnoreCase(String scopeName);

    boolean existsByScopeNameIgnoreCase(String scopeName);
}
