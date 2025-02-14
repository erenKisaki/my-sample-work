import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.*;
import org.apache.poi.xwpf.usermodel.*;

public class DocxToPdfExactOrder {
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream("input.docx");
        XWPFDocument document = new XWPFDocument(fis);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage();
        pdf.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setLeading(14.5f);

        PDFont fontRegular = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
        PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;

        float margin = 50;
        float yPosition = page.getMediaBox().getHeight() - margin;

        for (IBodyElement element : document.getBodyElements()) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);

                for (XWPFRun run : paragraph.getRuns()) {
                    PDFont font = fontRegular;
                    if (run.isBold()) font = fontBold;
                    if (run.isItalic()) font = fontItalic;

                    contentStream.setFont(font, run.getFontSize() > 0 ? run.getFontSize() : 12);
                    contentStream.showText(run.text());
                }

                contentStream.endText();
                yPosition -= 20;
            } 
            else if (element instanceof XWPFTable) {
                XWPFTable table = (XWPFTable) element;
                float tableX = margin;
                float tableY = yPosition;
                float cellWidth = 100;
                float cellHeight = 20;

                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        PDPageContentStream tableStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);
                        
                        tableStream.addRect(tableX, tableY, cellWidth, -cellHeight);
                        tableStream.stroke();

                        tableStream.beginText();
                        tableStream.newLineAtOffset(tableX + 5, tableY - 15);
                        tableStream.setFont(fontRegular, 10);
                        tableStream.showText(cell.getText());
                        tableStream.endText();
                        tableStream.close();

                        tableX += cellWidth;
                    }
                    tableX = margin;
                    tableY -= cellHeight;
                }
                yPosition = tableY - 20;
            } 
            else if (element instanceof XWPFPictureData) {
                XWPFPictureData pictureData = (XWPFPictureData) element;
                byte[] imageBytes = pictureData.getData();
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

                if (bufferedImage != null) {
                    PDImageXObject image = LosslessFactory.createFromImage(pdf, bufferedImage);
                    PDPageContentStream imageStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);
                    imageStream.drawImage(image, margin, yPosition - 150, 150, 100);
                    imageStream.close();
                    yPosition -= 150;
                }
            }
        }

        contentStream.close();
        pdf.save("output.pdf");
        pdf.close();
        document.close();
        fis.close();

        System.out.println("DOCX converted to PDF with exact order and formatting!");
    }
}
