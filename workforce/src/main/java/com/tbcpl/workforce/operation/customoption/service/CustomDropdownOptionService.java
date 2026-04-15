package com.tbcpl.workforce.operation.customoption.service;

import com.tbcpl.workforce.operation.customoption.dto.request.CustomOptionRequest;
import com.tbcpl.workforce.operation.customoption.dto.response.CustomOptionResponse;

import java.util.List;

public interface CustomDropdownOptionService {
    List<CustomOptionResponse> getOptions(String fieldName);
    CustomOptionResponse saveOption(CustomOptionRequest request);
}