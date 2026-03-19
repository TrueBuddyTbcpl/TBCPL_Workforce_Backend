package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MaterialSeizedRequest {

    private List<MaterialSeizedItemRequest> materialSeized = new ArrayList<>();
}
