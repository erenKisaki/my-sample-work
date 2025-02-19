import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class ExcelToPDF {
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream("input.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setLeading(14);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);

        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String value = getCellValue(cell, formulaEvaluator);
                    contentStream.showText(value + "   ");
                }
                contentStream.newLine();
            }
        }

        contentStream.endText();
        contentStream.close();

        // Extract and insert images
        List<XSSFPictureData> pictures = workbook.getAllPictures();
        for (XSSFPictureData picture : pictures) {
            byte[] imageBytes = picture.getData();
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            File imageFile = File.createTempFile("excel_image", ".png");
            ImageIO.write(bImage, "png", imageFile);
            PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), pdf);

            PDPage imgPage = new PDPage();
            pdf.addPage(imgPage);
            PDPageContentStream imgStream = new PDPageContentStream(pdf, imgPage);
            imgStream.drawImage(pdImage, 50, 200, 400, 300);
            imgStream.close();
        }

        pdf.save("output.pdf");
        pdf.close();
        workbook.close();
        fis.close();

        System.out.println("✅ Excel to PDF conversion completed with tables, formulas, and images!");
    }

    private static String getCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return formulaEvaluator.evaluateInCell(cell).toString();  // Fix for formulas
            default: return "";
        }
    }
}
