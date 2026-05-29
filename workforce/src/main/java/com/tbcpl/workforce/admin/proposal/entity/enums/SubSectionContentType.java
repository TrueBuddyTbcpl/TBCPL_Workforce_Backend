package com.tbcpl.workforce.admin.proposal.entity.enums;

public enum SubSectionContentType {

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
     * Key-value table rows.
     * content = JSON array of objects → [{ key, value }]
     */
    TABLE
}