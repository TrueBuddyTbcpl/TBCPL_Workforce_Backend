package com.tbcpl.workforce.operation.prereport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLeadStep1Request {

    private LocalDate dateInfoReceived;
    private String clientSpocName;
    private String clientSpocContact;
}
