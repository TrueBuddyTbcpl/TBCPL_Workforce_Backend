package com.tbcpl.workforce.operation.profile.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RelationshipNature {
    OWNERSHIP, CONTROL, REPRESENTATION, PARTNERSHIP, SUPPLIER, CLIENT;

    @JsonCreator
    public static RelationshipNature fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return RelationshipNature.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
