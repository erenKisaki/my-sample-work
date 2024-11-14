import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class ZipFolder {
    public static void zipFolder(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDirPath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    // Get the relative path from the source directory
                    Path relativePath = sourceDirPath.relativize(path);
                    ZipEntry zipEntry = new ZipEntry(relativePath.toString().replace("\\", "/"));
                    try {
                        zos.putNextEntry(zipEntry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        System.err.println("Error zipping file: " + path + " - " + e.getMessage());
                    }
                });
        }
    }

    public static void main(String[] args) {
        Path sourceDir = Paths.get("path/to/your/folder"); // Replace with your folder path
        Path zipFile = Paths.get("path/to/your/folder.zip"); // Replace with the output zip file path

        try {
            zipFolder(sourceDir, zipFile);
            System.out.println("Folder successfully zipped to: " + zipFile);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
