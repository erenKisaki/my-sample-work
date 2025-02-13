        XWPFDocument document = new XWPFDocument(fis);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage();
        pdf.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setLeading(14.5f);

        // Default fonts
        PDFont fontRegular = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
        PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;

        float margin = 50;
        float yPosition = page.getMediaBox().getHeight() - margin;

        contentStream.beginText();
        contentStream.setFont(fontRegular, 12);
        contentStream.newLineAtOffset(margin, yPosition);

        // ✅ Extract and Format Text
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (paragraph.getRuns().isEmpty()) continue;

            for (XWPFRun run : paragraph.getRuns()) {
                PDFont font = fontRegular;
                if (run.isBold()) font = fontBold;
                if (run.isItalic()) font = fontItalic;

                contentStream.setFont(font, run.getFontSize() > 0 ? run.getFontSize() : 12);
                contentStream.showText(run.text());
            }

            contentStream.newLine();
        }

        contentStream.endText();
        contentStream.close();

        // ✅ Extract Images
        for (XWPFPictureData pictureData : document.getAllPictures()) {
            byte[] imageBytes = pictureData.getData();
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            if (bufferedImage != null) {
                PDImageXObject image = LosslessFactory.createFromImage(pdf, bufferedImage);
                PDPageContentStream imageStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);
                imageStream.drawImage(image, margin, yPosition - 150, 150, 100); // Adjust position & size
                imageStream.close();
                yPosition -= 150;
            }
        }

        // ✅ Extract Tables
        for (XWPFTable table : document.getTables()) {
            float tableX = margin;
            float tableY = yPosition;
            float cellWidth = 100;
            float cellHeight = 20;

            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    PDPageContentStream tableStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true);
                    
                    // Draw cell border
                    tableStream.setStrokingColor(Color.BLACK);
                    tableStream.addRect(tableX, tableY, cellWidth, -cellHeight);
                    tableStream.stroke();

                    // Write text inside cell
                    tableStream.beginText();
                    tableStream.setFont(fontRegular, 10);
                    tableStream.newLineAtOffset(tableX + 5, tableY - 15);
                    tableStream.showText(cell.getText());
                    tableStream.endText();

                    tableStream.close();

                    tableX += cellWidth;
                }
                tableX = margin;
                tableY -= cellHeight;
            }
        }

        pdf.save("output.pdf");
        pdf.close();
        document.close();
        fis.close();
