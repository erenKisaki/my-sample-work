import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;

public class ExcelToPDF {
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream("input.xlsx");
        Workbook workbook = WorkbookFactory.create(fis);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setLeading(15);
        contentStream.newLineAtOffset(50, 750);

        // Read sheets and write to PDF
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    contentStream.showText(cell.toString() + "   ");
                }
                contentStream.newLine();
            }
        }

        contentStream.endText();
        contentStream.close();

        // Extract and insert images
        if (workbook instanceof org.apache.poi.xssf.usermodel.XSSFWorkbook) {
            List<XSSFPictureData> pictures = ((org.apache.poi.x
