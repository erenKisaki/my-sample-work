import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class ExcelToPDFConverter {
    public static void convertExcelToPDF(byte[] excelData, OutputStream outputStream) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(excelData);
             Workbook workbook = WorkbookFactory.create(inputStream);
             PDDocument pdf = new PDDocument()) {

            DataFormatter formatter = new DataFormatter();
            float margin = 50;
            float yStart = PDRectangle.A4.getHeight() - margin;
            float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;
            float rowHeight = 20;
            float cellMargin = 5;

            for (Sheet sheet : workbook) {
                PDPage page = new PDPage(PDRectangle.A4);
                pdf.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
                
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yStart);
                contentStream.showText("Sheet: " + sheet.getSheetName());
                contentStream.endText();

                float yPosition = yStart - 30;
                int numCols = sheet.getRow(0) != null ? sheet.getRow(0).getLastCellNum() : 0;
                float colWidth = tableWidth / (numCols == 0 ? 1 : numCols);

                contentStream.setLineWidth(0.5f);

                for (Row row : sheet) {
                    if (yPosition < margin) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        pdf.addPage(page);
                        contentStream = new PDPageContentStream(pdf, page);
                        yPosition = yStart;
                    }

                    float xPosition = margin;
                    for (Cell cell : row) {
                        String cellValue = formatter.formatCellValue(cell);
                        drawCell(contentStream, xPosition, yPosition, colWidth, rowHeight, cellValue, cellMargin);
                        xPosition += colWidth;
                    }
                    yPosition -= rowHeight;
                }

                contentStream.close();
            }

            pdf.save(outputStream);
        }
    }

    private static void drawCell(PDPageContentStream contentStream, float x, float y, float width, float height, String text, float margin) throws IOException {
        contentStream.setStrokingColor(0, 0, 0);
        contentStream.addRect(x, y, width, -height);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(x + margin, y - height + margin);
        contentStream.showText(text);
        contentStream.endText();
    }
}
