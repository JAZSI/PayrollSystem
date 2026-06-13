package com.com253.payrollsystem.thirteenthmonth;

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

/** Renders a 13th-month statement as a PDF (OpenPDF). */
@Service
public class ThirteenthMonthPdfService {

    private static final Color HEADER_BG = new Color(33, 33, 33);
    private static final Color LIGHT = new Color(245, 245, 245);

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
    private static final Font SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(90, 90, 90));
    private static final Font LABEL = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(70, 70, 70));
    private static final Font VALUE = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font NET = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.WHITE);

    public byte[] render(ThirteenthMonthEntry entry, int year) {
        Document document = new Document(PageSize.A4, 48, 48, 48, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph title = new Paragraph("ABC Company", TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("13th Month Pay — " + year, SUBTITLE);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(16);
            document.add(subtitle);

            PdfPTable info = new PdfPTable(2);
            info.setWidthPercentage(100);
            addCell(info, "Employee ID", LABEL, Element.ALIGN_LEFT, LIGHT);
            addCell(info, entry.getEmployeeId(), VALUE, Element.ALIGN_LEFT, LIGHT);
            addCell(info, "Name", LABEL, Element.ALIGN_LEFT, Color.WHITE);
            addCell(info, entry.getEmployeeName(), VALUE, Element.ALIGN_LEFT, Color.WHITE);
            addCell(info, "Total Basic Earned", LABEL, Element.ALIGN_LEFT, LIGHT);
            addCell(info, peso(entry.getTotalBasic()), VALUE, Element.ALIGN_RIGHT, LIGHT);
            addCell(info, "Divided by", LABEL, Element.ALIGN_LEFT, Color.WHITE);
            addCell(info, "12 months", VALUE, Element.ALIGN_RIGHT, Color.WHITE);
            document.add(info);

            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingAfter(4);
            document.add(spacer);

            PdfPTable net = new PdfPTable(2);
            net.setWidthPercentage(100);
            PdfPCell label = new PdfPCell(new Phrase("13TH MONTH PAY", NET));
            PdfPCell value = new PdfPCell(new Phrase(peso(entry.getAmount()), NET));
            for (PdfPCell c : new PdfPCell[]{label, value}) {
                c.setBackgroundColor(HEADER_BG);
                c.setPadding(8);
                c.setBorder(0);
            }
            value.setHorizontalAlignment(Element.ALIGN_RIGHT);
            net.addCell(label);
            net.addCell(value);
            document.add(net);

            document.close();
            return out.toByteArray();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to generate 13th-month PDF", e);
        }
    }

    private static void addCell(PdfPTable table, String text, Font font, int align, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(align);
        c.setBackgroundColor(bg);
        c.setPadding(5);
        c.setBorder(0);
        table.addCell(c);
    }

    private static String peso(double v) {
        return String.format("PHP %,.2f", v);
    }
}
