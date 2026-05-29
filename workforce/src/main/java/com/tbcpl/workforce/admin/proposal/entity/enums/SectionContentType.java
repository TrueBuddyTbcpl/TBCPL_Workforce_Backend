package com.tbcpl.workforce.admin.proposal.entity.enums;

import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

public enum SectionContentType {

    /**
     * Plain paragraph text.
     * content = "We conducted a thorough investigation..."
     */
    TEXT,

    /**
     * Bullet / numbered list.
     * content = JSON array of strings → ["Item one", "Item two"]
     */
    LIST,

    /**
     * Fee / pricing block.
     * content = JSON array of objects → [{ label, amount, note }]
     */
    FEE,

    /**
     * Key-value table rows.
     * content = JSON array of objects → [{ key, value }]
     */
    TABLE
}

