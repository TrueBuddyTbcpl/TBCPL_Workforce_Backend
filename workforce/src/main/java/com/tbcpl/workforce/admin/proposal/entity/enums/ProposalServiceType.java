package com.tbcpl.workforce.admin.proposal.entity.enums;

public enum ProposalServiceType {
    DUE_DILIGENCE_INVESTIGATION("Due Diligence Investigation"),
    INTELLECTUAL_PROPERTY_INVESTIGATION("Intellectual Property Investigation (IPR)"),
    CORPORATE_INVESTIGATION("Corporate Investigation"),
    ONLINE_MONITORING("Online Monitoring");

    private final String displayName;

    ProposalServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
