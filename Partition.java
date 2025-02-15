        HWPFDocument document = new HWPFDocument(fis);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage();
        pdf.addPage(page);

        float margin = 50;
        float yPosition = page.getMediaBox().getHeight() - margin;
        float pageWidth = page.getMediaBox().getWidth() - 2 * margin;

        PDFont fontRegular = PDType1Font.HELVETICA;
        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setLeading(14.5f);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);

        Range range = document.getRange();
        PicturesTable picturesTable = document.getPicturesTable();
        List<Picture> pictures = picturesTable.getAllPictures();
        int pictureIndex = 0;

        for (int i = 0; i < range.numParagraphs(); i++) {
            Paragraph paragraph = range.getParagraph(i);
            String text = paragraph.text().trim();

            // **🔹 Check if paragraph contains an image placeholder**
            if (text.contains("\u0008") && pictureIndex < pictures.size()) {  // `\u0008` represents an image placeholder
                contentStream.endText();
                contentStream.close();

                Picture picture = pictures.get(pictureIndex++);
                byte[] imageBytes = picture.getContent();
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

                if (bufferedImage != null) {
                    PDImageXObject image = LosslessFactory.createFromImage(pdf, bufferedImage);
                    PDPageContentStream imageStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);

                    // **🔹 Maintain original image dimensions**
                    float imgWidth = picture.getWidth();
                    float imgHeight = picture.getHeight();

                    // Scale down if image is wider than page
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

                    // **🔹 Draw Image on PDF**
                    imageStream.drawImage(image, margin, yPosition - imgHeight, imgWidth, imgHeight);
                    imageStream.close();
                    yPosition -= (imgHeight + 10);
                }

                // Restart text stream after image
                contentStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
            } else {
                // **🔹 Process text**
                contentStream.setFont(fontRegular, 12);
                contentStream.showText(text);
                contentStream.newLine();
                yPosition -= 20;
            }

            // **🔹 Check for new page if needed**
            if (yPosition < 50) {
                contentStream.endText();
                contentStream.close();
                page = new PDPage();
                pdf.addPage(page);
                contentStream = new PDPageContentStream(pdf, page);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, page.getMediaBox().getHeight() - margin);
                yPosition = page.getMediaBox().getHeight() - margin;
            }
        }

        contentStream.endText();
        contentStream.close();
