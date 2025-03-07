public static byte[] convertAFPtoPDF(final byte[] afpFile) throws HdpException {
    LOG.info("Inside the method of convertAFPtoPDF");
    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        List<byte[]> tiffImgList = AFPConverter.processAfpFile(afpFile);
        try (PDDocument document = new PDDocument()) {
            tiffImgList.stream().forEach(tiffImg -> {
                try {
                    InputStream is = new ByteArrayInputStream(tiffImg);
                    ImageInputStream iis = ImageIO.createImageInputStream(is);
                    Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
                    ImageReader reader = iterator.next();
                    reader.setInput(iis, false, true);
                    int nbPages = reader.getNumImages(true);

                    for (int p = 0; p < nbPages; p++) {
                        BufferedImage bufferedImage = reader.read(p, reader.getDefaultReadParam());
                        
                        // Set the PDF page size same as the image
                        float width = bufferedImage.getWidth();
                        float height = bufferedImage.getHeight();
                        PDPage page = new PDPage(new PDRectangle(width, height));
                        document.addPage(page);

                        PDImageXObject imageXObject = LosslessFactory.createFromImage(document, bufferedImage);
                        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                            content.drawImage(imageXObject, 0, 0, width, height);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("ERROR CONVERTING TIF image to PDF using PDFBOX - Message=[{}], trying ITEXT ", e.getMessage());
                    throw new HdpException(new Message(e.getMessage(), CommonErrors.HDP_ERROR.getCode(), MessageType.ERROR));
                }
            });
            document.save(os);
        }
        LOG.info("Successfully converted from [AFP] Non Annotated Image file to PDF");
        return os.toByteArray();
    } catch (Exception e) {
        throw new HdpException(new Message(e.getMessage(), CommonErrors.HDP_ERROR.getCode(), MessageType.ERROR));
    }
}
