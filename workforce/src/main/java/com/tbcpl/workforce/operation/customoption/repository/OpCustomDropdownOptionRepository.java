package com.tbcpl.workforce.operation.customoption.repository;

import com.tbcpl.workforce.operation.customoption.entity.OpCustomDropdownOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpCustomDropdownOptionRepository extends JpaRepository<OpCustomDropdownOption, Long> {

    List<OpCustomDropdownOption> findByFieldNameOrderByCreatedAtAsc(String fieldName);

    boolean existsByFieldNameAndValue(String fieldName, String value);
}