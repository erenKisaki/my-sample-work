        HWPFDocument document = new HWPFDocument(fis);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage();
        pdf.addPage(page);
        
        float margin = 50;
        float yPosition = page.getMediaBox().getHeight() - margin;
        float pageWidth = page.getMediaBox().getWidth() - 2 * margin;

        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setLeading(14.5f);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);

        PicturesTable picturesTable = document.getPicturesTable();
        for (Picture picture : picturesTable.getAllPictures()) {
            contentStream.endText();
            contentStream.close();

            byte[] imageBytes = picture.getContent();
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            if (bufferedImage != null) {
                PDImageXObject image = LosslessFactory.createFromImage(pdf, bufferedImage);
                PDPageContentStream imageStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);

                // Get image size in points (from Picture object)
                float imgWidth = picture.getWidth();
                float imgHeight = picture.getHeight();

                // Ensure image fits page width
                if (imgWidth > pageWidth) {
                    float scale = pageWidth / imgWidth;
                    imgWidth *= scale;
                    imgHeight *= scale;
                }

                // Add new page if needed
                if (yPosition - imgHeight < margin) {
                    page = new PDPage();
                    pdf.addPage(page);
                    imageStream.close();
                    imageStream = new PDPageContentStream(pdf, page);
                    yPosition = page.getMediaBox().getHeight() - margin;
                }

                // Draw Image
                imageStream.drawImage(image, margin, yPosition - imgHeight, imgWidth, imgHeight);
                imageStream.close();
                yPosition -= (imgHeight + 10);
            }

            contentStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
        }

        contentStream.endText();
        contentStream.close();
