package com.tbcpl.workforce.ttr.repository;

import com.tbcpl.workforce.ttr.entity.TtrCompletionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TtrCompletionRecordRepository extends JpaRepository<TtrCompletionRecord, Long> {

    Page<TtrCompletionRecord> findByTtrIdOrderByCompletedAtDesc(Long ttrId, Pageable pageable);

    long countByTtrId(Long ttrId);

    @Query("SELECT COALESCE(MAX(r.cycleNumber), 0) FROM TtrCompletionRecord r WHERE r.ttr.id = :ttrId")
    int findMaxCycleNumberByTtrId(@Param("ttrId") Long ttrId);
}