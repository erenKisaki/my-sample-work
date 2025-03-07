import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.*;

public class ExcelToPDFConverter {
    private static final float MARGIN = 50;
    private static final float ROW_HEIGHT = 20;
    private static final float CELL_MARGIN = 5;
    private static final PDFont FONT = PDType1Font.HELVETICA;
    private static final int FONT_SIZE = 10;

    public static void convertExcelToPDF(InputStream excelInput, OutputStream pdfOutput) throws IOException {
        Workbook workbook = WorkbookFactory.create(excelInput);
        PDDocument pdf = new PDDocument();
        DataFormatter formatter = new DataFormatter();
        
        for (Sheet sheet : workbook) {
            if (isSheetEmpty(sheet, formatter)) continue;
            PDPage page = new PDPage(PDRectangle.A4);
            pdf.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
            contentStream.setFont(FONT, FONT_SIZE);
            float yPosition = page.getMediaBox().getHeight() - MARGIN;
            int numCols = getMaxColumns(sheet);
            float tableWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;
            float colWidth = tableWidth / Math.max(1, numCols);

            for (Row row : sheet) {
                if (isRowEmpty(row, formatter)) continue;
                float xPosition = MARGIN;
                
                for (Cell cell : row) {
                    String cellValue = formatter.formatCellValue(cell).replaceAll("[\t\n\r]+", " ");
                    if (xPosition + colWidth > page.getMediaBox().getWidth() - MARGIN) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        pdf.addPage(page);
                        contentStream = new PDPageContentStream(pdf, page);
                        contentStream.setFont(FONT, FONT_SIZE);
                        yPosition = page.getMediaBox().getHeight() - MARGIN;
                        xPosition = MARGIN;
                    }
                    drawCell(contentStream, xPosition, yPosition, colWidth, ROW_HEIGHT, cellValue);
                    xPosition += colWidth;
                }
                yPosition -= ROW_HEIGHT;
                if (yPosition < MARGIN) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    pdf.addPage(page);
                    contentStream = new PDPageContentStream(pdf, page);
                    contentStream.setFont(FONT, FONT_SIZE);
                    yPosition = page.getMediaBox().getHeight() - MARGIN;
                }
            }
            contentStream.close();
        }
        pdf.save(pdfOutput);
        pdf.close();
        workbook.close();
    }

    private static boolean isSheetEmpty(Sheet sheet, DataFormatter formatter) {
        for (Row row : sheet) {
            if (!isRowEmpty(row, formatter)) return false;
        }
        return true;
    }

    private static boolean isRowEmpty(Row row, DataFormatter formatter) {
        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell).trim().isEmpty()) return false;
        }
        return true;
    }

    private static int getMaxColumns(Sheet sheet) {
        int maxCols = 0;
        for (Row row : sheet) {
            if (row.getLastCellNum() > maxCols) {
                maxCols = row.getLastCellNum();
            }
        }
        return maxCols;
    }

    private static void drawCell(PDPageContentStream contentStream, float x, float y, float width, float height, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x + CELL_MARGIN, y - height + CELL_MARGIN);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.addRect(x, y - height, width, height);
        contentStream.stroke();
    }
}
