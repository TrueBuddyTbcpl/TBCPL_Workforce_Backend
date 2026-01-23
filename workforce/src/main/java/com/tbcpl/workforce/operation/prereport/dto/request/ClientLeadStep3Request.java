package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.YesNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLeadStep3Request {

    private String entityName;
    private String suspectName;
    private String contactNumbers;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private List<OnlinePresenceRequest> onlinePresences;
    private String productDetails;
    private YesNo photosProvided;
    private YesNo videoProvided;
    private YesNo invoiceAvailable;
    private String sourceNarrative;
}
