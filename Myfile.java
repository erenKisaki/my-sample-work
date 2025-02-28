
    private static void drawWrappedText(PDPageContentStream contentStream, String text, float maxWidth) throws IOException {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float spaceWidth = 3;
        float textWidth = 0;

        for (String word : words) {
            float wordWidth = PDType1Font.HELVETICA.getStringWidth(word) / 1000 * 10;
            if (textWidth + wordWidth > maxWidth) {
                contentStream.showText(line.toString());
                contentStream.newLineAtOffset(0, -12);
                line = new StringBuilder();
                textWidth = 0;
            }
            line.append(word).append(" ");
            textWidth += wordWidth + spaceWidth;
        }
        contentStream.showText(line.toString().trim());
    }
