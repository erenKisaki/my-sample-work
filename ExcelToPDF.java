
            PDPage page = new PDPage(PDRectangle.A4);
            pdfDocument.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
            contentStream.setFont(PDType1Font.HELVETICA, 10);

            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float rowHeight = 20;
            float cellMargin = 2;

            Sheet sheet = workbook.getSheetAt(0);
            List<List<String>> tableData = new ArrayList<>();
            int numColumns = sheet.getRow(0).getPhysicalNumberOfCells();
            float[] columnWidths = new float[numColumns];

            // Read data and calculate column widths
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (int i = 0; i < numColumns; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = cell.toString();
                    rowData.add(value);

                    float textWidth = PDType1Font.HELVETICA.getStringWidth(value) / 1000 * 10; // Scale to font size
                    columnWidths[i] = Math.max(columnWidths[i], textWidth + cellMargin * 2);
                }
                tableData.add(rowData);
            }

            // Normalize column widths to fit within table width
            float totalWidth = 0;
            for (float width : columnWidths) {
                totalWidth += width;
            }
            float scaleFactor = tableWidth / totalWidth;
            for (int i = 0; i < columnWidths.length; i++) {
                columnWidths[i] *= scaleFactor;
            }

            // Draw table
            for (List<String> row : tableData) {
                if (yPosition - rowHeight < margin) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    pdfDocument.addPage(page);
                    contentStream = new PDPageContentStream(pdfDocument, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    yPosition = page.getMediaBox().getHeight() - margin;
                }

                float xPosition = margin;
                for (int i = 0; i < row.size(); i++) {
                    String text = row.get(i);
                    float colWidth = columnWidths[i];

                    // Draw cell border
                    contentStream.setLineWidth(1);
                    contentStream.moveTo(xPosition, yPosition);
                    contentStream.lineTo(xPosition + colWidth, yPosition);
                    contentStream.lineTo(xPosition + colWidth, yPosition - rowHeight);
                    contentStream.lineTo(xPosition, yPosition - rowHeight);
                    contentStream.closeAndStroke();

                    // Draw text
                    contentStream.beginText();
                    contentStream.newLineAtOffset(xPosition + cellMargin, yPosition - 15);
                    contentStream.showText(text);
                    contentStream.endText();

                    xPosition += colWidth;
                }
                yPosition -= rowHeight;
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
