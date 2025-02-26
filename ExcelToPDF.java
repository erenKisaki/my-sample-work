import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;

public class ExcelToPDFTable {
    public static void main(String[] args) throws Exception {
        InputStream excelStream = new FileInputStream("input.xlsx");
        Workbook workbook = new XSSFWorkbook(excelStream);

        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);

        // ✅ Use Courier instead of Helvetica (more Unicode support)
        contentStream.setFont(PDType1Font.COURIER, 10);
        contentStream.setLeading(15);

        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - 50;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float rowHeight = 20;
        int maxCols = getMaxColumns(workbook);
        float colWidth = tableWidth / maxCols;

        DataFormatter formatter = new DataFormatter();

        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yStart);

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (int i = 0; i < maxCols; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = formatter.formatCellValue(cell).trim();

                    // ✅ Fix: Convert unsupported characters
                    cellValue = filterUnsupportedCharacters(cellValue);

                    contentStream.showText(cellValue.isEmpty() ? " " : wrapText(cellValue, colWidth));
                    contentStream.newLineAtOffset(colWidth, 0);
                }
                contentStream.newLineAtOffset(-tableWidth, -rowHeight);
            }
        }

        contentStream.endText();
        contentStream.close();

        pdf.save("output.pdf");
        pdf.close();
        workbook.close();
    }

    private static int getMaxColumns(Workbook workbook) {
        int maxCols = 0;
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                maxCols = Math.max(maxCols, row.getLastCellNum());
            }
        }
        return maxCols == -1 ? 5 : maxCols; // Default to 5 columns if empty
    }

    private static String wrapText(String text, float colWidth) {
        int maxChars = (int) (colWidth / 6); // Adjust for Courier font spacing
        StringBuilder wrapped = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            wrapped.append(text, i, Math.min(i + maxChars, text.length())).append("\n");
            i += maxChars;
        }
        return wrapped.toString();
    }

    // ✅ Function to filter out unsupported characters
    private static String filterUnsupportedCharacters(String text) {
        StringBuilder filtered = new StringBuilder();
        for (char c : text.toCharArray()) {
            try {
                // Ensure character is renderable in PDFBox
                if (PDType1Font.COURIER.encode(Character.toString(c)) != null) {
                    filtered.append(c);
                } else {
                    filtered.append("?"); // Replace unsupported characters
                }
            } catch (Exception e) {
                filtered.append("?"); // Catch any encoding issues
            }
        }
        return filtered.toString();
    }
}
