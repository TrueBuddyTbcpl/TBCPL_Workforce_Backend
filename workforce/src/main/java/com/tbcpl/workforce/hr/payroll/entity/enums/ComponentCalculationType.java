package com.tbcpl.workforce.hr.payroll.entity.enums;

public enum ComponentCalculationType {
    FLAT_AMOUNT,        // Fixed Rs. amount per month
    PERCENTAGE_OF_BASIC,// X% of Basic salary
    PERCENTAGE_OF_CTC,  // X% of total CTC
    STATUTORY           // Computed by rule (PF=12% of Basic, PT=slab, etc.)
}