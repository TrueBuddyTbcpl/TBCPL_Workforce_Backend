package com.tbcpl.workforce.common.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.tbcpl.workforce.grnd_operation.entity.Loa;
import com.tbcpl.workforce.grnd_operation.entity.LoaAssets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Slf4j
public class LoaPdfGeneratorUtil {

    private final S3Service s3Service;
    public LoaPdfGeneratorUtil(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    private static final String COMPANY_NAME         = "TRUE BUDDY CONSULTING PRIVATE LIMITED";
    private static final String COMPANY_CIN          = "CIN Number : U93000DL2016PTC291039";
    private static final String COMPANY_ADDRESS      = "A-22, Sec-3, Noida-201301";
    private static final String COMPANY_EMAIL        = "Email : contact@tbcpl.co.in";
    private static final String CEO_NAME             = "Pradeep Sharma";
    private static final String CEO_TITLE            = "CEO";

    private static final Color HEADER_BLUE = new Color(0x77, 0xBD, 0xF8);
    private static final Color BLACK       = new Color(0x00, 0x00, 0x00);

    // Smaller logo to match sample
    private static final float LOGO_W      = 80f;
    private static final float LOGO_H      = 60f;
    private static final float SIGNATURE_W = 120f;
    private static final float SIGNATURE_H = 65f;
    private static final float STAMP_W     = 100f;
    private static final float STAMP_H     = 100f;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    // ─── Main ────────────────────────────────────────────────────────────────

    public byte[] generateLoaPdf(Loa loa, LoaAssets assets) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 72f, 72f, 60f, 60f);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Image logoImg      = safeLoad(assets == null ? null : assets.getLogoUrl(),      LOGO_W,      LOGO_H);
            Image stampImg     = safeLoad(assets == null ? null : assets.getStampUrl(),     STAMP_W,     STAMP_H);
            Image signatureImg = safeLoad(assets == null ? null : assets.getSignatureUrl(), SIGNATURE_W, SIGNATURE_H);

            // ── Fonts (all slightly smaller than before) ─────────────────────
            Font boldBlueTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  14f, HEADER_BLUE);  // company name
            Font normalBlue    = FontFactory.getFont(FontFactory.HELVETICA,        9f, HEADER_BLUE);  // CIN / address
            Font boldBlack     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  10f, BLACK);        // AUTHORITY LETTER
            Font normal        = FontFactory.getFont(FontFactory.HELVETICA,        10f, BLACK);        // body text
            Font bold11        = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  11f, BLACK);        // CEO name
            Font boldBlue10    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  10f, HEADER_BLUE);  // "For TRUE BUDDY..."
            Font boldMed       = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  10f, BLACK);        // footer company

            // 1. HEADER
            buildHeader(doc, logoImg, boldBlueTitle, normalBlue);

            // 2. DATE — right-aligned (matches sample)
            String dateStr = ordinalDate(loa.getCreatedAt().toLocalDate().format(DATE_FMT));
            Paragraph datePara = new Paragraph("Date: " + dateStr, normal);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            datePara.setSpacingBefore(20f);
            datePara.setSpacingAfter(14f);
            doc.add(datePara);

            // 3. TITLE — centered, bold, underlined
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, Font.UNDERLINE, BLACK);
            Paragraph title = new Paragraph("AUTHORITY LETTER", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(8f);
            title.setSpacingAfter(18f);
            doc.add(title);

            // 4. BODY
            addJustified(doc, String.format(
                    "This is to authorize %s, Associate in True Buddy Consulting Pvt. Ltd. " +
                            "A-22, sec \u2013 3, Noida - 201301 to conduct surveys and gather information " +
                            "of the activities of the counterfeit/Spurious product sellers and those who " +
                            "indulge in infringement of the brands and products of company\u2019s client %s.",
                    loa.getEmployeeName(), loa.getClientName()), normal, 0f, 10f);

            addJustified(doc, String.format(
                    "He/She is authorized to liaise with police and other law enforcement agencies " +
                            "to institute complaints against fraudulent activity on behalf of the client " +
                            "company %s.", loa.getClientName()), normal, 0f, 10f);

            addJustified(doc,
                    "This authorization is valid only for the country as specified above and does " +
                            "not envisage launch of any other legal action such as civil action or any other " +
                            "kind of activity apart from what is specified above.",
                    normal, 0f, 10f);

            addJustified(doc,
                    "The company duly ratifies and confirms to ratify all that the said attorney " +
                            "shall lawfully do or cause to be done within the authority so granted to him " +
                            "in respect of the operations under his control.",
                    normal, 0f, 10f);

            // 5. VALIDITY
            String validStr = ordinalDate(loa.getValidUpto().format(DATE_FMT));
            addLeft(doc, "This authority letter is valid up to " + validStr + ".",
                    normal, 0f, 30f);

            // 6. FOOTER
            buildFooter(doc, signatureImg, stampImg, bold11, boldBlue10, normal, boldMed);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("PDF generation failed for LOA: {}", loa.getLoaNumber(), e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    // ─── Header ──────────────────────────────────────────────────────────────
    //
    //  +─────────────────+──────────────────────────────────────────────────────+
    //  │  [LOGO 80×60]   │    TRUE BUDDY CONSULTING PRIVATE LIMITED  (right)   │
    //  │                 │    CIN Number : U93000DL2016PTC291039     (right)   │
    //  │                 │    A-22, Sec-3, Noida-201301              (right)   │
    //  +─────────────────+──────────────────────────────────────────────────────+
    //  (no separator line)

    private void buildHeader(Document doc,
                             Image logoImg,
                             Font boldBlueTitle,
                             Font normalBlue) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{ 22f, 78f });
        table.setSpacingAfter(4f);

        // ── Left: Logo ───────────────────────────────────────────────────────
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setPaddingRight(8f);
        if (logoImg != null) {
            logoImg.setAlignment(Image.ALIGN_LEFT);
            logoCell.addElement(logoImg);
        }
        table.addCell(logoCell);

        // ── Right: Company info — all RIGHT-aligned (matches sample exactly) ─
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph name = new Paragraph(COMPANY_NAME, boldBlueTitle);
        name.setAlignment(Element.ALIGN_RIGHT);
        name.setSpacingAfter(4f);
        infoCell.addElement(name);

        Paragraph cin = new Paragraph(COMPANY_CIN, normalBlue);
        cin.setAlignment(Element.ALIGN_RIGHT);
        cin.setSpacingAfter(3f);
        infoCell.addElement(cin);

        Paragraph addr = new Paragraph(COMPANY_ADDRESS, normalBlue);
        addr.setAlignment(Element.ALIGN_RIGHT);
        infoCell.addElement(addr);

        table.addCell(infoCell);
        doc.add(table);

        // ⛔ No separator line
    }

    // ─── Footer ──────────────────────────────────────────────────────────────
    //
    //  "For TRUE BUDDY CONSULTING PVT. LTD."   ← bold blue, left
    //
    //  +──────────────────────────────────────+──────────────────────────────+
    //  │  [SIGNATURE]  Authorised Signatory   │      [STAMP 100×100]        │
    //  │  Pradeep Sharma                      │                             │
    //  │  CEO                                 │                             │
    //  +──────────────────────────────────────+──────────────────────────────+
    //
    //  (spacer)
    //
    //         True Buddy Consulting Private Limited    ← centered
    //              Email : contact@tbcpl.co.in         ← centered

    private void buildFooter(Document doc,
                             Image signatureImg, Image stampImg,
                             Font bold11, Font boldBlue10,
                             Font normal, Font boldMed) throws DocumentException {



        // Signature row
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(4f);
        table.setSpacingAfter(8f);

        // ── Left: Signature image + "Authorised Signatory" inline + CEO name ─
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

        if (signatureImg != null) {
            // Put signature + label side by side using a nested 2-col table
            PdfPTable sigRow = new PdfPTable(2);
            sigRow.setWidthPercentage(100f);
            sigRow.setWidths(new float[]{ 45f, 55f });

            PdfPCell sigImgCell = new PdfPCell();
            sigImgCell.setBorder(Rectangle.NO_BORDER);
            sigImgCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            signatureImg.setAlignment(Image.ALIGN_LEFT);
            sigImgCell.addElement(signatureImg);
            sigRow.addCell(sigImgCell);

            PdfPCell sigLabelCell = new PdfPCell();
            sigLabelCell.setBorder(Rectangle.NO_BORDER);
            sigLabelCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            sigLabelCell.setPaddingBottom(8f);
            sigRow.addCell(sigLabelCell);

            leftCell.addElement(sigRow);
        } else {
            // Spacer when no signature image
            PdfPTable spacer = new PdfPTable(1);
            spacer.setWidthPercentage(100f);
            PdfPCell sp = new PdfPCell(new Phrase(" "));
            sp.setBorder(Rectangle.NO_BORDER);
            sp.setFixedHeight(SIGNATURE_H);
            spacer.addCell(sp);
            leftCell.addElement(spacer);

        }

        Paragraph ceoName = new Paragraph(CEO_NAME, bold11);
        ceoName.setSpacingBefore(4f);
        leftCell.addElement(ceoName);

        leftCell.addElement(new Paragraph(CEO_TITLE, normal));
        table.addCell(leftCell);

        // ── Right: Stamp ─────────────────────────────────────────────────────
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        if (stampImg != null) {
            stampImg.setAlignment(Image.ALIGN_RIGHT);
            rightCell.addElement(stampImg);
        }
        table.addCell(rightCell);

        doc.add(table);

        // ── Extra space before bottom company line (matches sample) ───────────
        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingBefore(40f);
        doc.add(spacer);

        // Centered company name + email
        Paragraph companyLine = new Paragraph("True Buddy Consulting Private Limited", boldMed);
        companyLine.setAlignment(Element.ALIGN_CENTER);
        companyLine.setSpacingAfter(2f);
        doc.add(companyLine);

        Paragraph emailLine = new Paragraph(COMPANY_EMAIL, normal);
        emailLine.setAlignment(Element.ALIGN_CENTER);
        doc.add(emailLine);
    }


    // ─── Image loader ────────────────────────────────────────────────────────

    private Image safeLoad(String url, float maxW, float maxH) {
        if (url == null || url.isBlank()) return null;
        try {
            // ✅ Extract S3 key from URL and fetch bytes directly
            String key = extractS3Key(url);
            byte[] bytes = s3Service.downloadFileBytes(key);
            Image img = Image.getInstance(bytes);
            img.scaleToFit(maxW, maxH);
            return img;
        } catch (Exception e) {
            log.warn("Could not load image from S3 key extracted from URL: {}", url);
            return null;
        }
    }

    private String extractS3Key(String url) {
        String marker = ".amazonaws.com/";
        int idx = url.indexOf(marker);
        if (idx == -1) {
            throw new IllegalArgumentException("Not a valid S3 URL: " + url);
        }
        return url.substring(idx + marker.length());
    }

    // ─── Paragraph helpers ───────────────────────────────────────────────────

    private void addLeft(Document doc, String text, Font font,
                         float before, float after) throws DocumentException {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_LEFT);
        p.setSpacingBefore(before);
        p.setSpacingAfter(after);
        doc.add(p);
    }

    private void addJustified(Document doc, String text, Font font,
                              float before, float after) throws DocumentException {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setSpacingBefore(before);
        p.setSpacingAfter(after);
        doc.add(p);
    }

    // ─── Date ordinal ────────────────────────────────────────────────────────

    private String ordinalDate(String formatted) {
        String[] parts = formatted.split(" ", 2);
        int day = Integer.parseInt(parts[0]);
        return day + ordinalSuffix(day) + " " + parts[1];
    }

    private String ordinalSuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        return switch (day % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}
