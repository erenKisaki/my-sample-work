
        Workbook workbook = WorkbookFactory.create(fis);
        PDDocument pdf = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        pdf.addPage(page);
        
        PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setLeading(15);
        contentStream.beginText();  // 🔹 FIX: Start text stream
        contentStream.newLineAtOffset(50, 750);

        // Read sheets and write to PDF in a structured format
        for (Sheet sheet : workbook) {
            contentStream.showText("Sheet: " + sheet.getSheetName());
            contentStream.newLine();
            
            for (Row row : sheet) {
                for (Cell cell : row) {
                    contentStream.showText(cell.toString() + " | ");  // 🔹 FIX: Table-like format
                }
                contentStream.newLine();
            }
        }
        
        contentStream.endText();
        contentStream.close();

        // Extract images with proper positioning
        if (workbook instanceof org.apache.poi.xssf.usermodel.XSSFWorkbook) {
            List<XSSFPictureData> pictures = ((org.apache.poi.xssf.usermodel.XSSFWorkbook) workbook).getAllPictures();
            for (XSSFPictureData picture : pictures) {
                byte[] imageBytes = picture.getData();
                BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                
                if (bImage != null) {
                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(pdf, imageBytes, "excel_image");
                    PDPage imgPage = new PDPage(PDRectangle.A4);
                    pdf.addPage(imgPage);
                    
                    PDPageContentStream imgStream = new PDPageContentStream(pdf, imgPage);
                    imgStream.drawImage(pdImage, 50, 300, 400, 300);  // 🔹 FIX: Proper image size
                    imgStream.close();
                }
            }
        }
