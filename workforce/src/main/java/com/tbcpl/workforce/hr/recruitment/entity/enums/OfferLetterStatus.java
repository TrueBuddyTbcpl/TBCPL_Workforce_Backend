package com.tbcpl.workforce.hr.recruitment.entity.enums;

public enum OfferLetterStatus {
    DRAFTED,    // Created but not yet sent
    SENT,       // Dispatched to candidate
    ACCEPTED,   // Candidate accepted the offer
    REJECTED,   // Candidate rejected the offer
    REVOKED,    // HR revoked the offer
    EXPIRED     // Offer expiry date passed without response
}