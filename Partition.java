  public static void zipFolder(final Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
            Files.walkFileTree(sourceDirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Get the relative path from the source directory
                    Path relativePath = sourceDirPath.relativize(file);
                    ZipEntry zipEntry = new ZipEntry(relativePath.toString().replace("\\", "/"));
                    zos.putNextEntry(zipEntry);
                    try (FileInputStream fis = new FileInputStream(file.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
