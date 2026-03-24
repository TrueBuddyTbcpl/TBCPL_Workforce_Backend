package com.tbcpl.workforce.admin.proposal;

public final class ProposalDefaultTexts {

    private ProposalDefaultTexts() {}

    public static final String BACKGROUND =
            "The Client Company has raised concerns regarding the possibility of unauthorized entities " +
                    "manufacturing, distributing, or supplying its proprietary products. In light of these concerns, " +
                    "the Client intends to verify the legitimacy and business practices of the suspect entity. " +
                    "To address this requirement, the Client has engaged True Buddy Consulting Private Limited to " +
                    "conduct an independent and discreet due diligence investigation on the suspect entity. " +
                    "The objective is to assess the entity's incorporation status, operational capabilities, product " +
                    "portfolio, and any potential involvement in unauthorized activities. If such activities are " +
                    "identified, True Buddy will undertake a covert test purchase to secure evidentiary samples and " +
                    "documentation. This proposal outlines the scope, methodology, and commercial terms for " +
                    "conducting the above due diligence exercise.";

    public static final String CONFIDENTIALITY =
            "We require strict adherence by our management and staff to ethical rules of our profession and " +
                    "company. TBCPL and its people maintain complete independence of interest in relationships with " +
                    "clients. In all aspects of our practice, our management and staff maintain a strict standard of " +
                    "confidentiality towards information obtained during carrying out our professional duties and will " +
                    "not disclose it without prior consent of the company. The above paragraph does not apply to any " +
                    "information: (a) At the time of being obtained by TBCPL is already within the public domain; " +
                    "(b) Which subsequently comes within the public domain other than by breach by TBCPL; " +
                    "(c) Is acquired by TBCPL from a third party who, to the best of TBCPL's knowledge, is rightfully " +
                    "in possession of it and free to disclose it.";

    public static final String CONCLUSION =
            "TBCPL thanks %s for reposing trust and assure you of our management's whole-hearted commitment " +
                    "to the project. We request you to indicate your concurrence with scope of work and terms and " +
                    "conditions of service delivery indicated in this proposal.";

    public static String conclusionFor(String clientName) {
        return String.format(CONCLUSION, clientName);
    }
}
