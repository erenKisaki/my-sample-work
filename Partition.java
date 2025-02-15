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

        for (int i = 0; i < range.numParagraphs(); i++) {
            Paragraph paragraph = range.getParagraph(i);

            if (picturesTable.hasPicture(paragraph)) {
                contentStream.endText();
                contentStream.close();

                Picture picture = picturesTable.extractPicture(paragraph, false);
                byte[] imageBytes = picture.getContent();
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

                if (bufferedImage != null) {
                    PDImageXObject image = LosslessFactory.createFromImage(pdf, bufferedImage);
                    PDPageContentStream imageStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);

                    float imgWidth = picture.getWidth();
                    float imgHeight = picture.getHeight();

                    if (imgWidth > pageWidth) {
                        float scale = pageWidth / imgWidth;
                        imgWidth *= scale;
                        imgHeight *= scale;
                    }

                    if (yPosition - imgHeight < margin) {
                        page = new PDPage();
                        pdf.addPage(page);
                        imageStream.close();
                        imageStream = new PDPageContentStream(pdf, page);
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }

                    imageStream.drawImage(image, margin, yPosition - imgHeight, imgWidth, imgHeight);
                    imageStream.close();
                    yPosition -= (imgHeight + 10);
                }

                contentStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
            } else {
                contentStream.setFont(fontRegular, 12);
                contentStream.showText(paragraph.text().trim());
                contentStream.newLine();
                yPosition -= 20;
            }

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
