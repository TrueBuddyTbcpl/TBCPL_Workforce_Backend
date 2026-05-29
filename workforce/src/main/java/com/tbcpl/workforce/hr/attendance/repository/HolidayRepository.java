package com.tbcpl.workforce.hr.attendance.repository;

import com.tbcpl.workforce.hr.attendance.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByHolidayYearAndIsActiveTrueOrderByHolidayDateAsc(Integer year);

    List<Holiday> findByHolidayYearAndLocationAndIsActiveTrueOrderByHolidayDateAsc(
            Integer year, String location);

    boolean existsByHolidayDateAndLocation(LocalDate date, String location);

    boolean existsByHolidayDateAndLocationAndIdNot(LocalDate date, String location, Long id);

    // Check if a given date is a holiday (any location or specific)
    @Query("SELECT COUNT(h) > 0 FROM Holiday h WHERE h.holidayDate = :date " +
            "AND h.isActive = true AND (h.location IS NULL OR h.location = :location)")
    boolean isHoliday(@Param("date") LocalDate date, @Param("location") String location);

    @Query("SELECT h FROM Holiday h WHERE h.holidayDate BETWEEN :from AND :to " +
            "AND h.isActive = true ORDER BY h.holidayDate ASC")
    List<Holiday> findHolidaysBetween(
            @Param("from") LocalDate from,
            @Param("to")   LocalDate to
    );
}