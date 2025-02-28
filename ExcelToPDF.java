        PDDocument pdf = new PDDocument();
        
        float margin = 50;
        float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;
        float rowHeight = 20;
        float colPadding = 5;
        
        // Determine column count
        int maxCols = 0;
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                maxCols = Math.max(maxCols, row.getLastCellNum());
            }
        }

        // Dynamically distribute column widths
        float colWidth = tableWidth / maxCols;

        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setLeading(14);
        
        float yStart = page.getMediaBox().getHeight() - margin;
        float yPosition = yStart;
        
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (yPosition - rowHeight < margin) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    pdf.addPage(page);
                    contentStream = new PDPageContentStream(pdf, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    yPosition = yStart;
                }

                float xPosition = margin;
                for (int i = 0; i < maxCols; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = getCellValue(cell, workbook.getCreationHelper().createFormulaEvaluator());

                    // Draw cell border
                    contentStream.setStrokingColor(Color.BLACK);
                    contentStream.addRect(xPosition, yPosition - rowHeight, colWidth, rowHeight);
                    contentStream.stroke();

                    // Handle text wrapping
                    contentStream.beginText();
                    contentStream.newLineAtOffset(xPosition + colPadding, yPosition - 15);
                    drawWrappedText(contentStream, value, colWidth - (2 * colPadding));
                    contentStream.endText();

                    xPosition += colWidth;
                }
                yPosition -= rowHeight;
            }
        }
        contentStream.close();
        
        // Process images below the table
        float imgX = margin;
        float imgY = yPosition - 100;
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

            imgY -= 160;
        }
