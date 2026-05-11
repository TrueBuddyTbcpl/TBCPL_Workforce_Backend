package com.tbcpl.workforce.ttr.repository;

import com.tbcpl.workforce.ttr.entity.TtrStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TtrStatusHistoryRepository extends JpaRepository<TtrStatusHistory, Long> {

    List<TtrStatusHistory> findByTtrIdOrderByChangedAtAsc(Long ttrId);
}