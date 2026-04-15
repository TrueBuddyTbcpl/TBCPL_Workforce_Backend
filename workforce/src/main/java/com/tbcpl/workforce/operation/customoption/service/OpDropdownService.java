package com.tbcpl.workforce.operation.customoption.service;

import java.util.List;

public interface OpDropdownService {

    void persistCustomOption(String fieldName, String value, String empId);

    List<String> getOptionsForField(String fieldName);
}