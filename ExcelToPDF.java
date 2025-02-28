import java.io.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ExcelToPDF {
    public static void main(String[] args) throws Exception {
        byte[] excelData = getExcelByteArray(); // Load your XLS file as byte array

        InputStream fs = new ByteArrayInputStream(excelData);
        HSSFWorkbook workbook = new HSSFWorkbook(fs);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setLeading(14);
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float rowHeight = 20;
        float tableX = margin;

        // Define column widths (adjust as needed)
        float[] colWidths = {70, 50, 90, 90, 80, 100, 60, 80, 80, 80, 100, 100};
        int numCols = colWidths.length;
        float tableY = yStart;

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (tableY < margin + rowHeight) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    pdf.addPage(page);
                    contentStream = new PDPageContentStream(pdf, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    tableY = yStart;
                }

                float x = tableX;
                for (int i = 0; i < numCols; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = getCellValue(cell, workbook.getCreationHelper().createFormulaEvaluator());

                    // Draw cell border
                    contentStream.setStrokingColor(Color.BLACK);
                    contentStream.addRect(x, tableY - rowHeight, colWidths[i], rowHeight);
                    contentStream.stroke();

                    // Write text
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x + 2, tableY - 15);
                    contentStream.showText(value);
                    contentStream.endText();

                    x += colWidths[i];
                }
                tableY -= rowHeight;
            }
        }
        contentStream.close();

        // Handle images
        for (HSSFPictureData picture : workbook.getAllPictures()) {
            byte[] imageBytes = picture.getData();
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            File imageFile = File.createTempFile("excel_image", ".png");
            ImageIO.write(bImage, "png", imageFile);
            PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), pdf);

            PDPage imgPage = new PDPage(PDRectangle.A4);
            pdf.addPage(imgPage);
            PDPageContentStream imgStream = new PDPageContentStream(pdf, imgPage);
            imgStream.drawImage(pdImage, 50, 200, 400, 300);
            imgStream.close();
        }

        pdf.save("ExcelToPDF.pdf");
        pdf.close();
        workbook.close();
        fs.close();
    }

    private static String getCellValue(Cell cell, FormulaEvaluator evaluator) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return evaluator.evaluate(cell).formatAsString();
            default:
                return "";
        }
    }

    private static byte[] getExcelByteArray() {
        // Implement logic to get XLS file as byte[]
        return new byte[0];
    }
}
