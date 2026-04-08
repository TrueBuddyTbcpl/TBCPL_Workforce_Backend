package com.tbcpl.workforce.operation.prereport.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.PreReportClientLead;
import com.tbcpl.workforce.operation.prereport.entity.PreReportTrueBuddyLead;
import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import com.tbcpl.workforce.operation.prereport.repository.PrereportCustomOptClientLeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PreReportPdfService {

    // ── Brand Colors ──────────────────────────────────────────────────────────
    private static final Color C_NAVY     = new Color(0x0f, 0x23, 0x40);
    private static final Color C_NAVY_MID = new Color(0x1a, 0x3a, 0x5c);
    private static final Color C_GOLD     = new Color(0xc8, 0x97, 0x2b);
    private static final Color C_HDR_BG   = new Color(0xe8, 0xee, 0xf5);
    private static final Color C_ROW_ALT  = new Color(0xf4, 0xf7, 0xfb);
    private static final Color C_BORDER   = new Color(0xb8, 0xc8, 0xd8);
    private static final Color C_TEXT     = Color.BLACK;
    private static final Color C_MUTED    = new Color(0x4a, 0x55, 0x68);
    private static final Color C_WHITE    = Color.WHITE;
    private static final Color C_LIGHT_BG = new Color(0xf8, 0xf9, 0xfb);

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    // ── ✅ FIX 1: Inject custom option repository ─────────────────────────────
    private final PrereportCustomOptClientLeadRepository customOptionRepository;

    public PreReportPdfService(PrereportCustomOptClientLeadRepository customOptionRepository) {
        this.customOptionRepository = customOptionRepository;
    }

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private Font font(int size, int style, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
    }
    private Font fontN(int size, Color color) { return font(size, Font.NORMAL, color); }
    private Font fontB(int size, Color color) { return font(size, Font.BOLD,   color); }
    private Font fontI(int size, Color color) { return font(size, Font.ITALIC, color); }

    // ── Public API ────────────────────────────────────────────────────────────
    public byte[] generatePdf(PreReport preReport,
                              PreReportClientLead clientLead,
                              PreReportTrueBuddyLead trueBuddyLead) {

        // ✅ FIX 2: Build option name lookup map for ALL steps of this leadType
        Map<Long, String> optionMap = customOptionRepository
                .findByLeadTypeAndDeletedFalse(preReport.getLeadType().name())
                .stream()
                .collect(Collectors.toMap(
                        o -> o.getId(),
                        o -> o.getOptionName(),   // ← confirm this getter exists on PrereportCustomOptClientLead
                        (a, b) -> a
                ));

        log.debug("[PdfService] Loaded {} custom options for leadType={}",
                optionMap.size(), preReport.getLeadType());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 48, 48);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // ✅ FIX 3: Pass optionMap into both builders
            if (LeadType.CLIENT_LEAD == preReport.getLeadType()) {
                buildClientLeadPdf(doc, preReport, clientLead, optionMap);
            } else {
                buildTrueBuddyLeadPdf(doc, preReport, trueBuddyLead, optionMap);
            }

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("PDF generation failed for reportId={}", preReport.getReportId(), e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String clientName(PreReport p) {
        return p.getClient() != null && p.getClient().getClientName() != null
                ? p.getClient().getClientName() : "N/A";
    }

    /** Resolves an optionId to its name using the map; fallback is safe label. */
    private String resolveOption(Long optionId, Map<Long, String> optionMap) {
        return optionMap.getOrDefault(optionId, "Custom Option #" + optionId);
    }

    // ── CLIENT LEAD PDF ───────────────────────────────────────────────────────
    // ✅ FIX 4: Added optionMap parameter
    private void buildClientLeadPdf(Document doc, PreReport preReport,
                                    PreReportClientLead cl,
                                    Map<Long, String> optionMap) throws DocumentException {
        String clientName = clientName(preReport);

        buildCoverPage(doc, preReport, clientName, "(Based on Client-Provided Information)");
        buildContentTitle(doc, "PRELIMINARY LEAD ASSESSMENT REPORT",
                "(Based on Client-Provided Information)", null);

        List<Section> sections = new ArrayList<>();

        // 1. Client & Case Details
        sections.add(new Section("Client & Case Details",
                buildDataTable(new float[]{44f, 56f}, new String[]{"Field", "Details"},
                        List.of(
                                row("Name of Client", clientName),
                                row("Date Information Received",
                                        cl.getDateInfoReceived() != null
                                                ? cl.getDateInfoReceived().format(DATE_FMT) : "N/A"),
                                row("Location (State / District / City)",
                                        joinNonNull(", ", cl.getState(), cl.getCity()))
                        ))));

        // 2. Scope
        List<String[]> scopeRows = new ArrayList<>();
        addBoolRow(scopeRows, "Due Diligence",                                          cl.getScopeDueDiligence());
        addBoolRow(scopeRows, "IPR Retailer / Wholesaler",             cl.getScopeIprRetailer());
        addBoolRow(scopeRows, "IPR Supplier",                          cl.getScopeIprSupplier());
        addBoolRow(scopeRows, "IPR Manufacturer / Packager / Warehouse", cl.getScopeIprManufacturer());
        addBoolRow(scopeRows, "Online Sample Purchase",                                 cl.getScopeOnlinePurchase());
        addBoolRow(scopeRows, "Offline Sample Purchase",                                cl.getScopeOfflinePurchase());

        // ✅ FIX: Resolve custom scope option IDs → names
        if (cl.getScopeCustomIds() != null) {
            for (Long id : cl.getScopeCustomIds()) {
                scopeRows.add(new String[]{resolveOption(id, optionMap)});
            }
        }
        if (!scopeRows.isEmpty())
            sections.add(new Section("Mandate / Scope Requested", buildCheckedList(scopeRows)));

        // 3. Information Received
        if (hasValue(cl.getEntityName(), cl.getContactNumbers(), cl.getProductDetails())) {
            sections.add(new Section("Information Received from Client",
                    buildDataTable(new float[]{50f, 50f}, new String[]{"Parameter", "Details"},
                            List.of(
                                    row("Name of Entity",                   cl.getEntityName()),
                                    row("Name of Suspect",                  cl.getSuspectName()),
                                    row("Contact Number(s)",                cl.getContactNumbers()),
                                    row("Address / Location", joinNonNull(", ",
                                            cl.getAddressLine1(), cl.getAddressLine2(),
                                            cl.getCity(), cl.getState(), cl.getPincode())),
                                    row("Product Details",                  cl.getProductDetails()),
                                    row("Product Photographs Provided",     mapYesNoEnum(cl.getPhotosProvided())),
                                    row("Video Evidence Provided",          mapYesNoEnum(cl.getVideoProvided())),
                                    row("Invoice / Bill Available",         mapYesNoEnum(cl.getInvoiceAvailable())),
                                    row("Source Narrative",                 cl.getSourceNarrative())
                            ))));
        }

        // 4. Verification
        List<String[]> verRows = new ArrayList<>();
        addVerRow(verRows, "Case Discussion with Client Team",         cl.getVerificationClientDiscussion(), cl.getVerificationClientDiscussionNotes());
        addVerRow(verRows, "Internet / OSINT Search",                  cl.getVerificationOsint(),            cl.getVerificationOsintNotes());
        addVerRow(verRows, "Marketplace Verification",                 cl.getVerificationMarketplace(),      cl.getVerificationMarketplaceNotes());
        addVerRow(verRows, "Pretext Calling (if applicable)",          cl.getVerificationPretextCalling(),   cl.getVerificationPretextCallingNotes());
        addVerRow(verRows, "Preliminary Product Image Review",         cl.getVerificationProductReview(),    cl.getVerificationProductReviewNotes());

        // ✅ FIX: Resolve custom verification option IDs → names
        if (cl.getVerificationCustomData() != null) {
            for (var item : cl.getVerificationCustomData()) {
                if ("DONE".equalsIgnoreCase(item.getStatus())) {
                    String name = resolveOption(item.getOptionId(), optionMap);
                    verRows.add(new String[]{name, "DONE",
                            item.getNotes() != null ? item.getNotes() : ""});
                }
            }
        }
        if (!verRows.isEmpty())
            sections.add(new Section("Preliminary Verification Conducted by True Buddy",
                    buildVerificationTable(verRows)));

        // 5. Key Observations
        List<String[]> obsRows = new ArrayList<>();
        addTextRow(obsRows, "Availability of Identifiable Target",          cl.getObsIdentifiableTarget());
        addTextRow(obsRows, "Traceability of Entity / Contact",             cl.getObsTraceability());
        addTextRow(obsRows, "Product Visibility / Market Presence",         cl.getObsProductVisibility());
        addTextRow(obsRows, "Indications of Counterfeiting / Lookalike",    cl.getObsCounterfeitingIndications());
        addTextRow(obsRows, "Evidentiary Gaps Identified",                  cl.getObsEvidentiary_gaps());

        // ✅ FIX: Resolve custom observation option IDs → names
        if (cl.getObservationsCustomData() != null) {
            for (var item : cl.getObservationsCustomData()) {
                String name = resolveOption(item.getOptionId(), optionMap);
                // Show custom text if available, else just the option name
                String display = (item.getText() != null && !item.getText().isBlank())
                        ? name + ": " + item.getText()
                        : name;
                obsRows.add(new String[]{display});
            }
        }
        if (!obsRows.isEmpty())
            sections.add(new Section("Key Observations", buildCheckedList(obsRows)));

        // 6. Quality Assessment
        if (cl.getQaCompleteness() != null || cl.getQaAccuracy() != null) {
            sections.add(new Section("Information Quality Assessment",
                    buildDataTable(new float[]{56f, 44f}, new String[]{"Parameter", "Assessment"},
                            List.of(
                                    row("Completeness of Initial Information",         mapEnum(cl.getQaCompleteness())),
                                    row("Accuracy of Case Description",                mapEnum(cl.getQaAccuracy())),
                                    row("Independent Client Investigation Conducted",  mapYesNoEnum(cl.getQaIndependentInvestigation())),
                                    row("Prior Confrontation with Seller / Suspect",   mapYesNoEnum(cl.getQaPriorConfrontation())),
                                    row("Risk of Information Contamination",           mapEnum(cl.getQaContaminationRisk()))
                            ))));
        }

        // 7. Assessment
        if (cl.getAssessmentOverall() != null) {
            String assessmentVal = mapClientAssessment(cl.getAssessmentOverall());
            sections.add(new Section("True Buddy's Preliminary Assessment",
                    buildAssessmentBlock("Overall Assessment of Lead", assessmentVal,
                            cl.getAssessmentRationale(), "Rationale")));
        }

        // 8. Recommendations
        List<String[]> recRows = new ArrayList<>();
        addBoolRow(recRows, "Market Survey",                   cl.getRecMarketSurvey());
        addBoolRow(recRows, "Covert Investigation",                             cl.getRecCovertInvestigation());
        addBoolRow(recRows, "Test Purchase",                         cl.getRecTestPurchase());
        addBoolRow(recRows, "Enforcement Action",                        cl.getRecEnforcementAction());
        addBoolRow(recRows, "Additional Information Required",      cl.getRecAdditionalInfo());
        addBoolRow(recRows, "Closure / Hold",                                   cl.getRecClosureHold());

        // ✅ FIX: Resolve custom recommendation option IDs → names
        if (cl.getRecCustomIds() != null) {
            for (Long id : cl.getRecCustomIds()) {
                recRows.add(new String[]{resolveOption(id, optionMap)});
            }
        }
        if (!recRows.isEmpty())
            sections.add(new Section("Recommended Way Forward", buildCheckedList(recRows)));

        // 9. Remarks
        if (hasValue(cl.getRemarks()))
            sections.add(new Section("Remarks", buildRemarksBlock(cl.getRemarks())));

        renderSections(doc, sections);

        String disclaimer = cl.getCustomDisclaimer() != null ? cl.getCustomDisclaimer() :
                "This preliminary assessment is prepared solely on the basis of information provided by the client. " +
                        "True Buddy assumes the information to be complete and accurate at this stage. In the event that the " +
                        "information is found to be incomplete, inaccurate, or misleading during subsequent investigation or " +
                        "field deployment, additional costs towards team mobilisation and preliminary investigation shall be " +
                        "applicable as per the approved proposal. This document does not constitute a final investigative " +
                        "report or legal opinion.";
        buildDisclaimer(doc, sections.size() + 1, disclaimer);
    }

    // ── TRUE BUDDY LEAD PDF ───────────────────────────────────────────────────
    // ✅ FIX 4: Added optionMap parameter
    private void buildTrueBuddyLeadPdf(Document doc, PreReport preReport,
                                       PreReportTrueBuddyLead tb,
                                       Map<Long, String> optionMap) throws DocumentException {
        String clientName = clientName(preReport);

        buildCoverPage(doc, preReport, clientName,
                "(Lead Generated Through True Buddy Intelligence Network)");
        buildContentTitle(doc, "PRELIMINARY LEAD ASSESSMENT REPORT",
                "(Lead Generated Through True Buddy Intelligence Network)",
                "[Confidential -- Client-Sanitised Version]");

        List<Section> sections = new ArrayList<>();

        // 1. Client & Case Reference
        sections.add(new Section("Client & Case Reference",
                buildDataTable(new float[]{44f, 56f}, new String[]{"Field", "Details"},
                        List.of(
                                row("Client Name",               clientName),
                                row("Product Category",          resolveCustomEnum(tb.getProductCategory(),    tb.getProductCategoryCustomText())),
                                row("Type of Infringement",      resolveCustomEnum(tb.getInfringementType(),   tb.getInfringementTypeCustomText())),
                                row("Date of Internal Lead Generation",
                                        tb.getDateInternalLeadGeneration() != null
                                                ? tb.getDateInternalLeadGeneration().format(DATE_FMT) : "N/A"),
                                row("Broad Geography",           tb.getBroadGeography()),
                                row("Nature of Entity",          resolveCustomEnum(tb.getNatureOfEntity(),     tb.getNatureOfEntityCustomText())),
                                row("Reason of Suspicion",       mapReasonList(tb.getReasonOfSuspicion(),     tb.getReasonOfSuspicionCustomText())),
                                row("Expected Seizure",          tb.getExpectedSeizure())
                        ))));

        // 2. Scope
        List<String[]> scopeRows = new ArrayList<>();
        addBoolRow(scopeRows, "IPR Investigation -- Supplier Level",               tb.getScopeIprSupplier());
        addBoolRow(scopeRows, "IPR Investigation -- Manufacturer / Packager",      tb.getScopeIprManufacturer());
        addBoolRow(scopeRows, "IPR Investigation -- Stockist / Warehouse",         tb.getScopeIprStockist());
        addBoolRow(scopeRows, "Covert Market Verification",                        tb.getScopeMarketVerification());
        addBoolRow(scopeRows, "Evidential Test Purchase (ETP)",                    tb.getScopeEtp());
        addBoolRow(scopeRows, "Enforcement Facilitation (If Applicable)",          tb.getScopeEnforcement());

        // ✅ FIX: Resolve custom scope option IDs → names
        if (tb.getScopeCustomIds() != null) {
            for (Long id : tb.getScopeCustomIds()) {
                scopeRows.add(new String[]{resolveOption(id, optionMap)});
            }
        }
        if (!scopeRows.isEmpty())
            sections.add(new Section("Mandate / Scope Proposed", buildCheckedList(scopeRows)));

        // 3. Lead Description
        List<String[]> descRows = new ArrayList<>();
        if (tb.getIntelNature()        != null) descRows.add(row("Nature of Intelligence",        resolveCustomEnum(tb.getIntelNature(),        tb.getIntelNatureCustomText())));
        if (tb.getSuspectedActivity()  != null) descRows.add(row("Type of Suspected Activity",   resolveCustomEnum(tb.getSuspectedActivity(),  tb.getSuspectedActivityCustomText())));
        if (tb.getProductSegment()     != null) descRows.add(row("Product Segment",              resolveCustomEnum(tb.getProductSegment(),     tb.getProductSegmentCustomText())));
        if (tb.getRepeatIntelligence() != null) descRows.add(row("Repeat Intelligence Indicator", mapYesNoEnum(tb.getRepeatIntelligence())));
        if (tb.getMultiBrandRisk()     != null) descRows.add(row("Multi-Brand Exposure Risk",    mapYesNoEnum(tb.getMultiBrandRisk())));
        if (!descRows.isEmpty())
            sections.add(new Section("High-Level Lead Description (Sanitised)",
                    buildDataTable(new float[]{46f, 54f}, new String[]{"Parameter", "Description"}, descRows)));

        // 4. Verification
        List<String[]> verRows = new ArrayList<>();
        addVerRow(verRows, "Internal Intelligence Corroboration",     tb.getVerificationIntelCorroboration(),  tb.getVerificationIntelCorroborationNotes());
        addVerRow(verRows, "OSINT / Market Footprint Review",         tb.getVerificationOsint(),               tb.getVerificationOsintNotes());
        addVerRow(verRows, "Pattern Mapping (Similar Past Cases)",    tb.getVerificationPatternMapping(),      tb.getVerificationPatternMappingNotes());
        addVerRow(verRows, "Jurisdiction Feasibility Review",         tb.getVerificationJurisdiction(),        tb.getVerificationJurisdictionNotes());
        addVerRow(verRows, "Risk & Sensitivity Assessment",           tb.getVerificationRiskAssessment(),      tb.getVerificationRiskAssessmentNotes());

        // ✅ FIX: Resolve custom verification option IDs → names
        if (tb.getVerificationCustomData() != null) {
            for (var item : tb.getVerificationCustomData()) {
                if ("DONE".equalsIgnoreCase(item.getStatus())) {
                    String name = resolveOption(item.getOptionId(), optionMap);
                    verRows.add(new String[]{name, "DONE",
                            item.getNotes() != null ? item.getNotes() : ""});
                }
            }
        }
        if (!verRows.isEmpty())
            sections.add(new Section("Preliminary Verification Conducted by True Buddy",
                    buildVerificationTable(verRows)));

        // 5. Key Observations
        List<String[]> obsRows = new ArrayList<>();
        if (tb.getObsOperationScale()        != null) obsRows.add(row("Scale of Suspected Operations",             mapEnum(tb.getObsOperationScale())));
        if (tb.getObsCounterfeitLikelihood() != null) obsRows.add(row("Likelihood of Counterfeit Activity",        mapEnum(tb.getObsCounterfeitLikelihood())));
        if (tb.getObsBrandExposure()         != null) obsRows.add(row("Potential Brand Exposure",                  resolveCustomEnum(tb.getObsBrandExposure(), tb.getObsBrandExposureCustomText())));
        if (tb.getObsEnforcementSensitivity()!= null) obsRows.add(row("Enforcement Sensitivity (Political / Local)", mapEnum(tb.getObsEnforcementSensitivity())));

        // ✅ FIX: Resolve custom observation option IDs → names
        if (tb.getObservationsCustomData() != null) {
            for (var item : tb.getObservationsCustomData()) {
                String name = resolveOption(item.getOptionId(), optionMap);
                String display = (item.getText() != null && !item.getText().isBlank())
                        ? name + ": " + item.getText()
                        : name;
                obsRows.add(new String[]{display, ""});
            }
        }
        if (!obsRows.isEmpty())
            sections.add(new Section("Key Observations (Client-Safe)",
                    buildDataTable(new float[]{52f, 48f}, new String[]{"Observation", "Assessment / Description"}, obsRows)));

        // 6. Risk Assessment
        List<String[]> riskRows = new ArrayList<>();
        if (tb.getRiskSourceReliability()    != null) riskRows.add(row("Source Reliability (Internal Assessment)",  mapEnum(tb.getRiskSourceReliability())));
        if (tb.getRiskClientConflict()       != null) riskRows.add(row("Risk of Cross-Client Conflict",             mapEnum(tb.getRiskClientConflict())));
        if (tb.getRiskImmediateAction()      != null) riskRows.add(row("Suitability for Immediate Action",          mapYesNoEnum(tb.getRiskImmediateAction())));
        if (tb.getRiskControlledValidation() != null) riskRows.add(row("Requirement for Controlled Validation",     mapYesNoEnum(tb.getRiskControlledValidation())));

        // ✅ FIX: Resolve custom risk option IDs → names (TrueBuddy only)
        if (tb.getRiskCustomData() != null) {
            for (var item : tb.getRiskCustomData()) {
                String name = resolveOption(item.getOptionId(), optionMap);
                riskRows.add(row(name, item.getValue() != null ? item.getValue() : "N/A"));
            }
        }
        if (!riskRows.isEmpty())
            sections.add(new Section("Information Integrity & Risk Assessment",
                    buildDataTable(new float[]{56f, 44f}, new String[]{"Parameter", "Assessment"}, riskRows)));

        // 7. Assessment
        if (tb.getAssessmentOverall() != null) {
            String assessmentVal = mapTbAssessment(tb.getAssessmentOverall());
            sections.add(new Section("True Buddy's Preliminary Assessment",
                    buildAssessmentBlock("Overall Assessment of Lead", assessmentVal,
                            tb.getAssessmentRationale(), "Assessment Rationale")));
        }

        // 8. Recommendations
        List<String[]> recRows = new ArrayList<>();
        addBoolRow(recRows, "Covert Validation",            tb.getRecCovertValidation());
        addBoolRow(recRows, "ETP (Evidence Test Purchase)",                  tb.getRecEtp());
        addBoolRow(recRows, "Market Reconnaissance",          tb.getRecMarketReconnaissance());
        addBoolRow(recRows, "Enforcement Planning",                      tb.getRecEnforcementDeferred());
        addBoolRow(recRows, "Continued Monitoring",                      tb.getRecContinuedMonitoring());
        addBoolRow(recRows, "Client Segregation",      tb.getRecClientSegregation());

        // ✅ FIX: Resolve custom recommendation option IDs → names
        if (tb.getRecCustomIds() != null) {
            for (Long id : tb.getRecCustomIds()) {
                recRows.add(new String[]{resolveOption(id, optionMap)});
            }
        }
        if (!recRows.isEmpty())
            sections.add(new Section("Recommended Way Forward", buildCheckedList(recRows)));

        // 9. Remarks
        if (hasValue(tb.getRemarks()))
            sections.add(new Section("Remarks", buildRemarksBlock(tb.getRemarks())));

        renderSections(doc, sections);

        String disclaimer = tb.getCustomDisclaimer() != null ? tb.getCustomDisclaimer() :
                "This preliminary assessment is based on internally generated intelligence and limited " +
                        "non-intrusive verification. Specific source details, identities, and methods have been " +
                        "deliberately withheld to preserve confidentiality and prevent information contamination. " +
                        "This document does not constitute a final investigative report or confirmation of infringement. " +
                        "Any further action will be undertaken only upon written client approval and under a " +
                        "client-specific scope of work. Additional costs for validation, mobilisation, or enforcement " +
                        "shall be applicable as per the agreed proposal.";
        buildDisclaimer(doc, sections.size() + 1, disclaimer);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ALL METHODS BELOW ARE UNCHANGED FROM ORIGINAL
    // ═══════════════════════════════════════════════════════════════════════════

    private void buildCoverPage(Document doc, PreReport preReport,
                                String clientName, String subtitle) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBackgroundColor(C_NAVY);
        companyCell.setPadding(20);
        companyCell.setBorder(Rectangle.NO_BORDER);
        Paragraph companyName = new Paragraph("TRUE BUDDY CONSULTING PVT. LTD.", fontB(16, C_WHITE));
        companyName.setAlignment(Element.ALIGN_CENTER);
        companyCell.addElement(companyName);
        Paragraph tagline = new Paragraph(
                "Due Diligence  |  IPR Investigation  |  Fraud Investigation  |  Market Surveys",
                fontI(8, new Color(0xa8, 0xc4, 0xdc)));
        tagline.setAlignment(Element.ALIGN_CENTER);
        companyCell.addElement(tagline);
        header.addCell(companyCell);
        doc.add(header);

        PdfPTable goldBar = new PdfPTable(1);
        goldBar.setWidthPercentage(100);
        PdfPCell goldCell = new PdfPCell(new Phrase(" "));
        goldCell.setBackgroundColor(C_GOLD);
        goldCell.setFixedHeight(4f);
        goldCell.setBorder(Rectangle.NO_BORDER);
        goldBar.addCell(goldCell);
        doc.add(goldBar);
        doc.add(new Paragraph("\n"));

        Paragraph title = new Paragraph("PRELIMINARY LEAD ASSESSMENT REPORT", fontB(14, C_NAVY));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        doc.add(title);

        Paragraph sub = new Paragraph(subtitle, fontI(9, C_MUTED));
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingBefore(5);
        sub.setSpacingAfter(30);
        doc.add(sub);

        PdfPTable metaTable = new PdfPTable(new float[]{34f, 66f});
        metaTable.setWidthPercentage(100);
        metaTable.setSpacingBefore(10);

        String leadTypeDisplay = LeadType.TRUEBUDDY_LEAD == preReport.getLeadType()
                ? "True Buddy Lead" : "Client Lead";
        String statusDisplay = preReport.getReportStatus() != null
                ? toTitleCase(preReport.getReportStatus().name()) : "N/A";
        String dateDisplay = preReport.getCreatedAt() != null
                ? preReport.getCreatedAt().format(DATE_FMT) : "N/A";

        addMetaRow(metaTable, "CLIENT",         clientName,               0);
        addMetaRow(metaTable, "REPORT ID",      preReport.getReportId(),  1);
        addMetaRow(metaTable, "LEAD TYPE",      leadTypeDisplay,          2);
        addMetaRow(metaTable, "STATUS",         statusDisplay,            3);
        addMetaRow(metaTable, "DATE GENERATED", dateDisplay,              4);
        doc.add(metaTable);
        doc.add(new Paragraph("\n"));

        PdfPTable confTable = new PdfPTable(1);
        confTable.setWidthPercentage(100);
        PdfPCell confCell = new PdfPCell();
        confCell.setBackgroundColor(new Color(0xee, 0xf3, 0xf8));
        confCell.setPadding(14);
        confCell.setBorderColorLeft(C_GOLD);
        confCell.setBorderWidthLeft(3f);
        confCell.setBorderColorTop(C_BORDER);
        confCell.setBorderColorBottom(C_BORDER);
        confCell.setBorderColorRight(C_BORDER);
        confCell.setBorderWidthTop(1f);
        confCell.setBorderWidthBottom(1f);
        confCell.setBorderWidthRight(1f);
        Paragraph confTitle = new Paragraph("CONFIDENTIAL DOCUMENT", fontB(11, C_NAVY));
        confTitle.setAlignment(Element.ALIGN_CENTER);
        confCell.addElement(confTitle);
        Paragraph confBody = new Paragraph(
                "This report contains sensitive information intended solely for authorised personnel.\n" +
                        "Unauthorised disclosure, reproduction, or distribution is strictly prohibited.",
                fontI(8, C_MUTED));
        confBody.setAlignment(Element.ALIGN_CENTER);
        confCell.addElement(confBody);
        confTable.addCell(confCell);
        doc.add(confTable);
        doc.newPage();
    }

    private void addMetaRow(PdfPTable table, String label, String value, int index) {
        Color labelBg = (index % 2 == 0) ? C_NAVY : C_NAVY_MID;
        Color valueBg = (index % 2 == 0) ? C_WHITE : C_ROW_ALT;
        PdfPCell labelCell = new PdfPCell(new Phrase(label, fontB(8, C_WHITE)));
        labelCell.setBackgroundColor(labelBg);
        labelCell.setPadding(8);
        labelCell.setBorderColor(C_BORDER);
        table.addCell(labelCell);
        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A",
                index == 0 ? fontB(10, C_NAVY) : fontN(9, C_TEXT)));
        valueCell.setBackgroundColor(valueBg);
        valueCell.setPadding(8);
        valueCell.setBorderColor(C_BORDER);
        table.addCell(valueCell);
    }

    private void buildContentTitle(Document doc, String line1, String line2,
                                   String line3) throws DocumentException {
        Paragraph p1 = new Paragraph(line1, fontB(13, C_NAVY));
        p1.setAlignment(Element.ALIGN_CENTER);
        p1.setSpacingBefore(10);
        doc.add(p1);
        Paragraph p2 = new Paragraph(line2, fontI(9, C_MUTED));
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.setSpacingBefore(4);
        doc.add(p2);
        if (line3 != null) {
            Paragraph p3 = new Paragraph(line3, fontB(9, C_NAVY));
            p3.setAlignment(Element.ALIGN_CENTER);
            p3.setSpacingBefore(3);
            p3.setSpacingAfter(15);
            doc.add(p3);
        } else {
            doc.add(new Paragraph("\n"));
        }
    }

    private void renderSections(Document doc, List<Section> sections) throws DocumentException {
        for (int i = 0; i < sections.size(); i++) {
            Section s = sections.get(i);
            doc.add(buildSectionHeader(i + 1, s.title()));
            for (Object block : s.blocks()) {
                if (block instanceof PdfPTable t) {
                    t.setSpacingAfter(10);
                    doc.add(t);
                } else if (block instanceof Paragraph p) {
                    doc.add(p);
                }
            }
            doc.add(new Paragraph("\n"));
        }
    }

    private PdfPTable buildSectionHeader(int number, String title) {
        PdfPTable table = new PdfPTable(new float[]{28f, 467f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        PdfPCell numCell = new PdfPCell(new Phrase(String.valueOf(number), fontB(11, C_WHITE)));
        numCell.setBackgroundColor(C_GOLD);
        numCell.setPadding(7);
        numCell.setBorderColor(C_GOLD);
        numCell.setBorderWidth(2f);
        numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(numCell);
        PdfPCell titleCell = new PdfPCell(new Phrase(title, fontB(11, C_WHITE)));
        titleCell.setBackgroundColor(C_NAVY);
        titleCell.setPadding(7);
        titleCell.setPaddingLeft(10);
        titleCell.setBorderColor(C_GOLD);
        titleCell.setBorderWidth(2f);
        table.addCell(titleCell);
        return table;
    }

    private PdfPTable buildDataTable(float[] widths, String[] headers, List<String[]> rows) {
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontB(9, C_NAVY)));
            cell.setBackgroundColor(C_HDR_BG);
            cell.setPadding(6);
            cell.setBorderColor(C_BORDER);
            cell.setBorderWidth(0.5f);
            table.addCell(cell);
        }
        for (int i = 0; i < rows.size(); i++) {
            Color bg = (i % 2 == 0) ? C_WHITE : C_ROW_ALT;
            String[] r = rows.get(i);
            PdfPCell k = new PdfPCell(new Phrase(r[0], fontB(9, C_NAVY)));
            k.setBackgroundColor(bg); k.setPadding(6);
            k.setBorderColor(C_BORDER); k.setBorderWidth(0.4f);
            table.addCell(k);
            String val = (r.length > 1 && r[1] != null && !r[1].isEmpty()) ? r[1] : "N/A";
            PdfPCell v = new PdfPCell(new Phrase(val, fontN(9, C_TEXT)));
            v.setBackgroundColor(bg); v.setPadding(6);
            v.setBorderColor(C_BORDER); v.setBorderWidth(0.4f);
            table.addCell(v);
        }
        return table;
    }

    private PdfPTable buildCheckedList(List<String[]> items) {
        PdfPTable table = new PdfPTable(new float[]{6f, 94f});
        table.setWidthPercentage(100);
        for (int i = 0; i < items.size(); i++) {
            Color bg = (i % 2 == 0) ? C_WHITE : C_ROW_ALT;
            PdfPCell arrow = new PdfPCell(new Phrase(">>", fontB(9, C_NAVY)));
            arrow.setBackgroundColor(bg); arrow.setPadding(6);
            arrow.setHorizontalAlignment(Element.ALIGN_CENTER);
            arrow.setBorderColor(C_BORDER); arrow.setBorderWidth(0.4f);
            table.addCell(arrow);
            PdfPCell label = new PdfPCell(new Phrase(items.get(i)[0], fontN(9, C_TEXT)));
            label.setBackgroundColor(bg); label.setPadding(6);
            label.setBorderColor(C_BORDER); label.setBorderWidth(0.4f);
            table.addCell(label);
        }
        return table;
    }

    private PdfPTable buildVerificationTable(List<String[]> rows) {
        PdfPTable table = new PdfPTable(new float[]{46f, 14f, 40f});
        table.setWidthPercentage(100);
        for (String h : new String[]{"Activity", "Status", "Key Notes"}) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontB(9, C_NAVY)));
            cell.setBackgroundColor(C_HDR_BG); cell.setPadding(6);
            cell.setBorderColor(C_BORDER); cell.setBorderWidth(0.5f);
            table.addCell(cell);
        }
        for (int i = 0; i < rows.size(); i++) {
            Color bg = (i % 2 == 0) ? C_WHITE : C_ROW_ALT;
            String[] r = rows.get(i);
            PdfPCell act = new PdfPCell(new Phrase(r[0], fontN(9, C_TEXT)));
            act.setBackgroundColor(bg); act.setPadding(6);
            act.setBorderColor(C_BORDER); act.setBorderWidth(0.4f);
            table.addCell(act);
            PdfPCell status = new PdfPCell(new Phrase("DONE", fontB(8, C_NAVY)));
            status.setBackgroundColor(C_HDR_BG); status.setPadding(6);
            status.setHorizontalAlignment(Element.ALIGN_CENTER);
            status.setBorderColor(C_BORDER); status.setBorderWidth(0.4f);
            table.addCell(status);
            PdfPCell notes = new PdfPCell(new Phrase(
                    r[2] != null && !r[2].isEmpty() ? r[2] : "-", fontN(9, C_TEXT)));
            notes.setBackgroundColor(bg); notes.setPadding(6);
            notes.setBorderColor(C_BORDER); notes.setBorderWidth(0.4f);
            table.addCell(notes);
        }
        return table;
    }

    private List<Object> buildAssessmentBlock(String label, String value,
                                              String rationale, String rationaleLabel) {
        List<Object> blocks = new ArrayList<>();
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(C_HDR_BG); cell.setPadding(9);
        cell.setBorderColorLeft(C_GOLD); cell.setBorderWidthLeft(4f);
        cell.setBorderColorTop(C_BORDER); cell.setBorderColorBottom(C_BORDER);
        cell.setBorderColorRight(C_BORDER);
        cell.setBorderWidthTop(1f); cell.setBorderWidthBottom(1f); cell.setBorderWidthRight(1f);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ":   ", fontB(10, C_NAVY)));
        p.add(new Chunk(value, fontB(10, C_TEXT)));
        cell.addElement(p);
        table.addCell(cell);
        blocks.add(table);
        if (rationale != null && !rationale.trim().isEmpty()) {
            Paragraph rLabel = new Paragraph(rationaleLabel + ":", fontB(9, C_NAVY));
            rLabel.setSpacingBefore(8); rLabel.setSpacingAfter(3);
            blocks.add(rLabel);
            blocks.add(new Paragraph(rationale.trim(), fontN(9, C_TEXT)));
        }
        return blocks;
    }

    private List<Object> buildRemarksBlock(String remarks) {
        Paragraph p = new Paragraph(remarks, fontN(9, C_TEXT));
        p.setSpacingAfter(10);
        return List.of(p);
    }

    private void buildDisclaimer(Document doc, int number, String text) throws DocumentException {
        doc.add(buildSectionHeader(number, "Disclaimer"));
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(C_LIGHT_BG); cell.setPadding(10);
        cell.setBorderColorLeft(C_NAVY_MID); cell.setBorderWidthLeft(3f);
        cell.setBorderColorTop(C_BORDER); cell.setBorderColorBottom(C_BORDER);
        cell.setBorderColorRight(C_BORDER);
        cell.setBorderWidthTop(0.5f); cell.setBorderWidthBottom(0.5f); cell.setBorderWidthRight(0.5f);
        Paragraph p = new Paragraph(text, fontI(8, C_MUTED));
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        cell.addElement(p);
        table.addCell(cell);
        doc.add(table);
    }

    // ── Section Record ────────────────────────────────────────────────────────
    private record Section(String title, List<Object> blocks) {
        Section(String title, PdfPTable table) {
            this(title, List.of(table));
        }
    }

    // ── Row Helpers ───────────────────────────────────────────────────────────
    private String[] row(String label, String value) {
        return new String[]{label, value != null && !value.isEmpty() ? value : "N/A"};
    }
    private void addBoolRow(List<String[]> list, String label, Boolean value) {
        if (Boolean.TRUE.equals(value)) list.add(new String[]{label});
    }
    private void addTextRow(List<String[]> list, String label, String value) {
        if (value != null && !value.trim().isEmpty()) list.add(new String[]{label});
    }
    private void addVerRow(List<String[]> list, String activity,
                           VerificationStatus status, String notes) {
        if (VerificationStatus.DONE == status)
            list.add(new String[]{activity, "DONE", notes != null ? notes : ""});
    }

    // ── Value Mappers ─────────────────────────────────────────────────────────
    private String toTitleCase(String value) {
        if (value == null || value.isEmpty()) return "N/A";
        return value.charAt(0) + value.substring(1).toLowerCase().replace("_", " ");
    }
    private String mapEnum(Enum<?> e) {
        return e == null ? "N/A" : toTitleCase(e.name());
    }
    private String mapYesNoEnum(YesNo v) {
        if (v == null) return "N/A";
        return v == YesNo.YES ? "Yes" : "No";
    }
    private String mapYesNoEnum(YesNoUnknown v) {
        if (v == null) return "N/A";
        return switch (v) {
            case YES       -> "Yes";
            case NO        -> "No";
            case NOT_KNOWN -> "Not Known";
        };
    }
    private String resolveCustomEnum(Enum<?> value, String customText) {
        if (value == null) return "N/A";
        if ("CUSTOM".equals(value.name()))
            return customText != null && !customText.trim().isEmpty() ? customText.trim() : "Custom";
        return toTitleCase(value.name());
    }
    private String mapClientAssessment(ClientLeadAssessment v) {
        if (v == null) return "N/A";
        return switch (v) {
            case ACTIONABLE                  -> "Actionable Lead";
            case ACTIONABLE_AFTER_VALIDATION -> "Potentially Actionable (Information Gaps Exist)";
            case NOT_ACTIONABLE              -> "Non-Actionable at Present";
            default                          -> toTitleCase(v.name());
        };
    }
    private String mapTbAssessment(TrueBuddyLeadAssessment v) {
        if (v == null) return "N/A";
        return switch (v) {
            case ACTIONABLE                  -> "Actionable (Subject to Client Alignment)";
            case ACTIONABLE_AFTER_VALIDATION -> "Actionable After Controlled Validation";
            case HOLD                        -> "Hold -- Monitoring Recommended";
            default                          -> toTitleCase(v.name());
        };
    }
    private String mapReasonList(List<ReasonOfSuspicion> values, String customText) {
        if (values == null || values.isEmpty()) return "N/A";
        List<String> mapped = new ArrayList<>();
        for (ReasonOfSuspicion v : values) {
            if (v == ReasonOfSuspicion.CUSTOM) {
                mapped.add(customText != null ? customText.trim() : "Custom");
            } else {
                mapped.add(toTitleCase(v.name()));
            }
        }
        return String.join(", ", mapped);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────
    private boolean hasValue(String... values) {
        for (String v : values)
            if (v != null && !v.trim().isEmpty()) return true;
        return false;
    }
    private String joinNonNull(String sep, String... parts) {
        List<String> filtered = new ArrayList<>();
        for (String p : parts)
            if (p != null && !p.trim().isEmpty()) filtered.add(p.trim());
        return filtered.isEmpty() ? "N/A" : String.join(sep, filtered);
    }
}