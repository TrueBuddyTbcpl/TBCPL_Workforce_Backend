package com.tbcpl.workforce.hr.attendance.repository;

import com.tbcpl.workforce.hr.attendance.entity.LeaveType;
import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    boolean existsByLeaveTypeNameIgnoreCase(String leaveTypeName);

    boolean existsByLeaveTypeNameIgnoreCaseAndIdNot(String leaveTypeName, Long id);

    Optional<LeaveType> findByLeaveTypeNameIgnoreCase(String leaveTypeName);

    List<LeaveType> findByIsActiveTrueOrderByLeaveTypeNameAsc();

    List<LeaveType> findByCategoryAndIsActiveTrue(LeaveCategory category);
}