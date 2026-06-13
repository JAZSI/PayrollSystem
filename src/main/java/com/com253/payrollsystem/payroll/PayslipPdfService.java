package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.payroll.dto.PayslipResponse;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

/** Renders a payslip as a PDF document (OpenPDF). */
@Service
public class PayslipPdfService {

    private static final Color HEADER_BG = new Color(33, 33, 33);
    private static final Color LIGHT = new Color(245, 245, 245);

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
    private static final Font SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(90, 90, 90));
    private static final Font SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private static final Font LABEL = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(70, 70, 70));
    private static final Font VALUE = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font NET = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.WHITE);

    public byte[] render(PayslipResponse s) {
        Document document = new Document(PageSize.A4, 48, 48, 48, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph title = new Paragraph("ABC Company", TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Employee Payslip", SUBTITLE);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(16);
            document.add(subtitle);

            document.add(infoTable(s));
            document.add(spacer());

            document.add(sectionHeader("ATTENDANCE SUMMARY"));
            document.add(rows(
                    "Total Hours Worked", hours(s.totalHours()),
                    "Overtime Hours", hours(s.overtimeHours()),
                    "Undertime Hours", hours(s.undertimeHours()),
                    "Absent Days", s.absentDays() + " day(s)"));
            document.add(spacer());

            document.add(sectionHeader("EARNINGS"));
            document.add(rows(
                    "Basic Pay", peso(s.basicPay()),
                    "Overtime Pay", peso(s.overtimePay()),
                    "Night Differential", peso(s.nightDiffPay()),
                    "Allowances", peso(s.allowances()),
                    "Gross Pay", peso(s.grossPay())));
            document.add(spacer());

            document.add(sectionHeader("DEDUCTIONS"));
            document.add(rows(
                    "SSS", peso(s.sss()),
                    "PhilHealth", peso(s.philhealth()),
                    "Pag-IBIG", peso(s.pagibig()),
                    "Withholding Tax", peso(s.tax()),
                    "Loan", peso(s.loan()),
                    "Other Deductions", peso(s.otherDeductions()),
                    "Undertime Penalty", peso(s.undertimePenalty()),
                    "Absence Penalty", peso(s.absencePenalty())));
            document.add(spacer());

            document.add(netTable(s.netPay()));

            document.close();
            return out.toByteArray();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to generate payslip PDF", e);
        }
    }

    private static PdfPTable infoTable(PayslipResponse s) {
        PdfPTable t = twoColumn();
        addCell(t, "Employee ID", LABEL, Element.ALIGN_LEFT, LIGHT);
        addCell(t, s.employeeId(), VALUE, Element.ALIGN_LEFT, LIGHT);
        addCell(t, "Name", LABEL, Element.ALIGN_LEFT, Color.WHITE);
        addCell(t, s.employeeName(), VALUE, Element.ALIGN_LEFT, Color.WHITE);
        addCell(t, "Type", LABEL, Element.ALIGN_LEFT, LIGHT);
        addCell(t, s.employeeTypeLabel(), VALUE, Element.ALIGN_LEFT, LIGHT);
        addCell(t, "Cut-off Period", LABEL, Element.ALIGN_LEFT, Color.WHITE);
        addCell(t, s.cutoffPeriod(), VALUE, Element.ALIGN_LEFT, Color.WHITE);
        return t;
    }

    private static PdfPTable sectionHeader(String text) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        PdfPCell c = new PdfPCell(new Phrase(text, SECTION));
        c.setBackgroundColor(HEADER_BG);
        c.setPadding(5);
        c.setBorder(0);
        t.addCell(c);
        return t;
    }

    private static PdfPTable rows(String... labelValuePairs) {
        PdfPTable t = twoColumn();
        for (int i = 0; i < labelValuePairs.length; i += 2) {
            Color bg = (i / 2) % 2 == 0 ? Color.WHITE : LIGHT;
            addCell(t, labelValuePairs[i], LABEL, Element.ALIGN_LEFT, bg);
            addCell(t, labelValuePairs[i + 1], VALUE, Element.ALIGN_RIGHT, bg);
        }
        return t;
    }

    private static PdfPTable netTable(double netPay) {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        PdfPCell label = new PdfPCell(new Phrase("NET PAY", NET));
        PdfPCell value = new PdfPCell(new Phrase(peso(netPay), NET));
        for (PdfPCell c : new PdfPCell[]{label, value}) {
            c.setBackgroundColor(HEADER_BG);
            c.setPadding(8);
            c.setBorder(0);
        }
        value.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(label);
        t.addCell(value);
        return t;
    }

    private static PdfPTable twoColumn() {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        return t;
    }

    private static void addCell(PdfPTable table, String text, Font font, int align, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(align);
        c.setBackgroundColor(bg);
        c.setPadding(5);
        c.setBorder(0);
        table.addCell(c);
    }

    private static Paragraph spacer() {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(4);
        return p;
    }

    private static String peso(double v) {
        return String.format("PHP %,.2f", v);
    }

    private static String hours(double v) {
        return String.format("%,.2f hrs", v);
    }
}
