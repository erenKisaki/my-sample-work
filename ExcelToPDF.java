import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.poi.ss.usermodel.*;
import java.io.*;

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

            for (Sheet sheet : workbook) {
                boolean hasContent = false;
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (!formatter.formatCellValue(cell).trim().isEmpty()) {
                            hasContent = true;
                            break;
                        }
                    }
                    if (hasContent) break;
                }
                if (!hasContent) continue; 
                
                PDPage page = new PDPage(PDRectangle.A4);
                pdf.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                
                float yPosition = yStart - 30;
                int numCols = sheet.getRow(0) != null ? sheet.getRow(0).getLastCellNum() : 0;
                float colWidth = tableWidth / Math.max(1, numCols);

                for (Row row : sheet) {
                    boolean isEmptyRow = true;
                    for (Cell cell : row) {
                        if (!formatter.formatCellValue(cell).trim().isEmpty()) {
                            isEmptyRow = false;
                            break;
                        }
                    }
                    if (isEmptyRow) continue;

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
