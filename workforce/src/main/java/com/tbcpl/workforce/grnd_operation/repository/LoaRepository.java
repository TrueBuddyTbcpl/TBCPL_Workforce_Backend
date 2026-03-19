package com.tbcpl.workforce.grnd_operation.repository;

import com.tbcpl.workforce.grnd_operation.entity.Loa;
import com.tbcpl.workforce.grnd_operation.entity.enums.LoaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaRepository extends JpaRepository<Loa, Long> {

    Optional<Loa> findByIdAndDeletedFalse(Long id);

    Page<Loa> findAllByDeletedFalse(Pageable pageable);

    Page<Loa> findAllByDeletedFalseAndStatus(LoaStatus status, Pageable pageable);
}
