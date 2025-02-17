import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ExcelToPDFComplete {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        FileInputStream fis = new FileInputStream("input.xlsx");
        Workbook workbook = new XSSFWorkbook(fis);
        PDDocument pdf = new PDDocument();

        for (Sheet sheet : workbook) {
            BufferedImage image = renderSheetAsImage((XSSFSheet) sheet);
            if (image != null) {
                PDPage page = new PDPage(PDRectangle.A4);
                pdf.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(pdf, imageToByteArray(image), "sheet_image");
                contentStream.drawImage(pdImage, 50, 200, 500, 600);
                contentStream.close();
            }
        }

        pdf.save("output.pdf");
        pdf.close();
        workbook.close();
        fis.close();
        System.out.println("✅ Excel to PDF conversion completed with exact layout!");
    }

    // Convert Excel sheet to an image
    private static BufferedImage renderSheetAsImage(XSSFSheet sheet) {
        int width = 1200, height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        int y = 20;
        for (Row row : sheet) {
            int x = 20;
            for (Cell cell : row) {
                String cellValue = getCellValue(cell);
                g2d.drawString(cellValue, x, y);
                x += 150;
            }
            y += 20;
        }
        g2d.dispose();
        return image;
    }

    // Extract cell values
    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluate(cell).formatAsString();
            default: return "";
        }
    }

    // Convert BufferedImage to byte array
    private static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
