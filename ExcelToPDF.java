      PDDocument pdf = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setLeading(14);

        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float rowHeight = 20;
        float yPosition = yStart;
        float tableX = margin;

        // Define column widths (adjust as needed)
        float[] colWidths = {70, 50, 90, 90, 80, 100, 60, 80, 80, 80, 100, 100};
        int numCols = colWidths.length;

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                // If not enough space, move to a new page
                if (yPosition - rowHeight < margin) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    pdf.addPage(page);
                    contentStream = new PDPageContentStream(pdf, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    yPosition = yStart;
                }

                float x = tableX;
                for (int i = 0; i < numCols; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = getCellValue(cell, evaluator);

                    // Draw cell border
                    contentStream.setStrokingColor(Color.BLACK);
                    contentStream.addRect(x, yPosition - rowHeight, colWidths[i], rowHeight);
                    contentStream.stroke();

                    // Write text with wrapping if necessary
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x + 2, yPosition - 15);
                    contentStream.showText(value.length() > 15 ? value.substring(0, 15) + "..." : value); // Truncate if needed
                    contentStream.endText();

                    x += colWidths[i];
                }
                yPosition -= rowHeight;
            }
        }
        contentStream.close();

        // Handle images
        for (HSSFPictureData picture : workbook.getAllPictures()) {
            byte[] imageBytes = picture.getData();
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            File imageFile = File.createTempFile("excel_image", ".png");
            ImageIO.write(bImage, "png", imageFile);
            PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), pdf);

            PDPage imgPage = new PDPage(PDRectangle.A4);
            pdf.addPage(imgPage);
            PDPageContentStream imgStream = new PDPageContentStream(pdf, imgPage);
            imgStream.drawImage(pdImage, 50, 200, 400, 300);
            imgStream.close();
        }
