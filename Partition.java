import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.*;

public class ExcelToPDFFormatted {
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream("input.xlsx");
        Workbook workbook = new XSSFWorkbook(fis);
        PDDocument pdf = new PDDocument();

        for (Sheet sheet : workbook) {
            PDPage page = new PDPage(PDRectangle.A4);
            pdf.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            
            int startX = 50, startY = 750, rowHeight = 20, colWidth = 100;
            
            for (Row row : sheet) {
                int cellX = startX;
                for (Cell cell : row) {
                    String cellValue = getCellValue(cell);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(cellX, startY);
                    contentStream.showText(cellValue);
                    contentStream.endText();
                    cellX += colWidth;
                }
                startY -= rowHeight;
            }
            
            contentStream.close();
        }

        pdf.save("output.pdf");
        pdf.close();
        workbook.close();
        fis.close();
        System.out.println("✅ Excel converted to PDF with proper formatting!");
    }

    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluate(cell).formatAsString();
            default: return "";
        }
    }
}
