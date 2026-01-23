package com.tbcpl.workforce.operation.prereport.repository;

import com.tbcpl.workforce.operation.prereport.entity.PreReportOnlinePresence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreReportOnlinePresenceRepository extends JpaRepository<PreReportOnlinePresence, Long> {

    List<PreReportOnlinePresence> findByPrereportId(Long prereportId);

    @Modifying
    @Query("DELETE FROM PreReportOnlinePresence pop WHERE pop.prereportId = :prereportId")
    void deleteByPrereportId(@Param("prereportId") Long prereportId);
}
