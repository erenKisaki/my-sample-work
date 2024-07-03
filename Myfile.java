import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils; // Ensure this import if you are using Apache Commons IO

@GET
@Path("image-conversion")
public void convertAndStoreImage(String sharedPath) throws Exception {
    try (Stream<java.nio.file.Path> paths = Files.walk(Paths.get("C:\\Git\\test"))) {
        paths.filter(Files::isRegularFile).forEach(file -> {
            InputStream inputStream = null;
            try {
                System.out.println(file.toString());
                File initialFile = new File(file.toString());
                inputStream = new FileInputStream(initialFile);
                byte[] imageBytes = IOUtils.toByteArray(inputStream);

                // Convert the unknown byte array to a PDF byte array
                byte[] pdfByteArray = PdfUtils.createPdfWithEmbeddedFile(imageBytes);

                // Get the user's home directory
                String userHome = System.getProperty("user.home");
                File outputFolder = new File(userHome + File.separator + "output");
                if (!outputFolder.exists()) {
                    outputFolder.mkdirs();
                }

                // Specify the output path for the PDF
                String outputPath = outputFolder.getPath() + File.separator + "output.pdf";
                PdfUtils.writeBytesToPdf(pdfByteArray, outputPath);
                System.out.println("PDF written to: " + outputPath);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    } catch (IOException e) {
        e.printStackTrace();
    }
}
