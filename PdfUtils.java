import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfUtils {
    public static byte[] createPdfWithEmbeddedFile(byte[] fileBytes) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("This PDF contains an embedded file.");
                contentStream.endText();
            }

            PDStream pdStream = new PDStream(document, new ByteArrayInputStream(fileBytes));
            pdStream.addCompression();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);

            return baos.toByteArray();
        }
    }

    public static void writeBytesToPdf(byte[] pdfBytes, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
        }
    }
}
