package org.royrvik.emrservice;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.util.Date;

public class PDFCreator {
    private static Font catFont = new Font(Font.FontFamily.HELVETICA, 18,
            Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.HELVETICA, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.HELVETICA, 12,
            Font.BOLD);
    private static Font smallerBold = new Font(Font.FontFamily.HELVETICA, 10,
            Font.BOLD);

    private static java.util.List<String> patientData;
    private static java.util.List<String> imagePaths;
    private static java.util.List<String> notes;

    public static ByteArrayOutputStream createPDF(java.util.List<String> p, java.util.List<String> i, java.util.List<String> n){

        try {
            patientData = p;
            imagePaths = i;
            notes = n;


            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos); //Fix this line lal
            document.open();
            addMetaData(document);
            addTitlePage(document);
            addContent(document);
            document.close();

            return baos;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void addMetaData(Document document) {
        document.addTitle("Examination");
        document.addSubject("Examination of patient");
        document.addKeywords("Java, PDF, sensitive");
        document.addAuthor("Andreas Røyrvik");
        document.addCreator("EMRApplication");
    }

    private static void addTitlePage(Document document)throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("VScan Report", catFont));
        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date           //Replace with getDoctor(), eller forkast metoden.
        preface.add(new Paragraph("Report generated by: Dr. " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallerBold));
        addEmptyLine(preface, 1);
        addEmptyLine(preface, 1);

        // TABLE GENERATION STARTS HERE
        // Needs getMethods for names and info to populate table cells.

        PdfPTable tbl = new PdfPTable(2);
        tbl.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase("First name: " + "Jens Kristian", smallBold));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("Patient ID: " + "", smallBold));
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("Last name: " + "", smallBold));
        cell.disableBorderSide(Rectangle.BOX);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("Exam: " + "", smallBold));
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("Date of birth: " + "", smallBold));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("Exam time: " + "", smallBold));
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("Exam comment: " + "", smallBold));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase("", smallBold));
        cell.disableBorderSide(Rectangle.BOX);
        tbl.addCell(cell);



        // Adding to document.
        document.add(preface);
        document.add(tbl);

        // Start a new page
        document.newPage();
    }

    private static void addContent(Document document) throws DocumentException {

        Anchor anchor = new Anchor("Examination result", catFont);
        anchor.setName("Examination result");


        int index = 1;
        int x = 0;
        for(String i : imagePaths){
            Chapter catPart = new Chapter(new Paragraph(anchor), 1);

            Paragraph subPara = new Paragraph("Image " + index++, subFont);
            Section subCatPart = catPart.addSection(subPara);

            Image image = null;
            try {
                int indentation = 0;
                image = Image.getInstance(i);
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - indentation) / image.getWidth()) * 100;
                image.scalePercent(scaler);
            }
            catch (IOException e) {e.printStackTrace(); }

            Paragraph imgParagraph = new Paragraph();
            imgParagraph.add(image);
            subCatPart.add(imgParagraph); //ADD image from path



            subPara = new Paragraph("Notes", subFont);
            subCatPart = catPart.addSection(subPara);
            subCatPart.add(new Paragraph(notes.get(x++)));


            document.add(catPart);


        }


    }


    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static PdfPTable createInfoTable()
            throws BadElementException {
        PdfPTable table = new PdfPTable(2);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

//        PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1",smallBold));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);
//
//        c1 = new PdfPCell(new Phrase("Table Header 2"));
//        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(c1);

        //table.addCell("Name",smallBold);
        Phrase p = new Phrase("Name", smallBold);
        table.addCell(p);
        table.addCell("Test testesen");

        table.addCell(new Phrase("SSN",smallBold));
        table.addCell("1245678910");

        table.addCell(new Phrase("Date",smallBold));
        table.addCell("09.10.2014");

        table.addCell(new Phrase("Department user",smallBold));
        table.addCell("rikardbe_emr");


       return table;

    }
}
