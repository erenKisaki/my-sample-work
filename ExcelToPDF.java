import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.*;

public class ExcelToPDFRefined {
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
            PDType1Font font = PDType1Font.HELVETICA;

            for (Sheet sheet : workbook) {
                List<List<String>> tableData = new ArrayList<>();
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    boolean isEmptyRow = true;
                    for (Cell cell : row) {
                        String cellValue = formatter.formatCellValue(cell).trim();
                        rowData.add(cellValue);
                        if (!cellValue.isEmpty()) isEmptyRow = false;
                    }
                    if (!isEmptyRow) tableData.add(rowData);
                }
                if (tableData.isEmpty()) continue;

                PDPage page = new PDPage(PDRectangle.A4);
                pdf.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
                contentStream.setFont(font, 10);
                float yPosition = yStart - 30;
                int numCols = tableData.get(0).size();
                float colWidth = tableWidth / Math.max(1, numCols);

                for (List<String> row : tableData) {
                    float xPosition = margin;
                    for (String cellValue : row) {
                        drawCell(contentStream, xPosition, yPosition, colWidth, rowHeight, cellValue, cellMargin, font);
                        xPosition += colWidth;
                    }
                    yPosition -= rowHeight;
                    if (yPosition < margin) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        pdf.addPage(page);
                        contentStream = new PDPageContentStream(pdf, page);
                        contentStream.setFont(font, 10);
                        yPosition = yStart - 30;
                    }
                }
                contentStream.close();
            }
            pdf.save(outputStream);
        }
    }

    private static void drawCell(PDPageContentStream contentStream, float x, float y, float width, float height, String text, float margin, PDType1Font font) throws IOException {
        contentStream.setStrokingColor(0, 0, 0);
        contentStream.addRect(x, y, width, -height);
        contentStream.stroke();
        contentStream.beginText();
        contentStream.setFont(font, 10);
        contentStream.newLineAtOffset(x + margin, y - height + margin);
        contentStream.showText(text);
        contentStream.endText();
    }
}
