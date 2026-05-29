package com.tbcpl.workforce.hr.recruitment.repository;

import com.tbcpl.workforce.hr.recruitment.entity.HrInterviewSchedule;
import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HrInterviewScheduleRepository
        extends JpaRepository<HrInterviewSchedule, Long> {

    List<HrInterviewSchedule> findByCandidateIdAndIsActiveTrueOrderByScheduledAtAsc(
            Long candidateId);

    Page<HrInterviewSchedule> findByStatusAndIsActiveTrueOrderByScheduledAtAsc(
            InterviewStatus status, Pageable pageable);

    // Interviews for a specific interviewer
    List<HrInterviewSchedule> findByInterviewerEmpIdAndIsActiveTrueOrderByScheduledAtAsc(
            String interviewerEmpId);

    // Upcoming interviews within a date range
    @Query("SELECT i FROM HrInterviewSchedule i " +
            "JOIN FETCH i.candidate c " +
            "WHERE i.scheduledAt BETWEEN :from AND :to " +
            "AND i.status = 'SCHEDULED' AND i.isActive = true " +
            "ORDER BY i.scheduledAt ASC")
    List<HrInterviewSchedule> findUpcomingInterviews(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );

    // Check for interviewer schedule conflict
    @Query("SELECT COUNT(i) > 0 FROM HrInterviewSchedule i " +
            "WHERE i.interviewerEmpId = :empId " +
            "AND i.status = 'SCHEDULED' AND i.isActive = true " +
            "AND i.scheduledAt BETWEEN :from AND :to")
    boolean hasInterviewerConflict(
            @Param("empId") String empId,
            @Param("from")  LocalDateTime from,
            @Param("to")    LocalDateTime to
    );
}