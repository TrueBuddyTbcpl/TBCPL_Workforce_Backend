package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.YesNo;
import com.tbcpl.workforce.operation.prereport.entity.enums.YesNoUnknown;
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
    private YesNoUnknown photosProvided;    // ← was YesNo
    private YesNoUnknown videoProvided;     // ← was YesNo
    private YesNoUnknown invoiceAvailable;
    private String sourceNarrative;
}
