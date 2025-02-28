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
        float tableX = margin;
        float tableY = yStart;

        float[] colWidths = {70, 50, 90, 90, 80, 100, 60, 80, 80, 80, 100, 100};
        int numCols = colWidths.length;

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (tableY < margin + rowHeight) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    pdf.addPage(page);
                    contentStream = new PDPageContentStream(pdf, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    tableY = yStart;
                }

                float x = tableX;
                for (int i = 0; i < Math.min(numCols, row.getLastCellNum()); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = getCellValue(cell, workbook.getCreationHelper().createFormulaEvaluator());

                    // Draw cell border
                    contentStream.setStrokingColor(Color.BLACK);
                    contentStream.addRect(x, tableY - rowHeight, colWidths[i], rowHeight);
                    contentStream.stroke();

                    // Write text
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x + 2, tableY - 15);
                    contentStream.showText(value);
                    contentStream.endText();

                    x += colWidths[i];
                }
                tableY -= rowHeight;
            }
        }
        contentStream.close();

        // Handle images and move them to the next available area
        float imgX = margin;
        float imgY = tableY - 100;
        for (HSSFPictureData picture : workbook.getAllPictures()) {
            if (imgY < margin + 100) {
                page = new PDPage(PDRectangle.A4);
                pdf.addPage(page);
                imgX = margin;
                imgY = yStart - 100;
            }

            byte[] imageBytes = picture.getData();
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            File imageFile = File.createTempFile("excel_image", ".png");
            ImageIO.write(bImage, "png", imageFile);
            PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.getAbsolutePath(), pdf);

            PDPageContentStream imgStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true);
            imgStream.drawImage(pdImage, imgX, imgY, 200, 150);
            imgStream.close();

            imgY -= 160; // Move down for the next image
        }
