package com.example.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

class ExcelMetadataExtractorTest {

    @Test
    void testExtractMetadata_withValidExcel() throws Exception {
        // Arrange: create a fake excel file with metadata
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        this.getClass().getResourceAsStream("/test-excel-with-metadata.xlsx"));

        // Act
        var result = ExcelMetadataExtractor.extractMetadata(multipartFile);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFileId()).isEqualTo("12345");
        assertThat(result.getSessionId()).isEqualTo("test-session");
        assertThat(result.getFileName()).isEqualTo("test.xlsx");
    }

    @Test
    void testExtractMetadata_withInvalidFile_throwsException() {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "bad.txt", "text/plain", "invalid".getBytes());

        assertThrows(IOException.class,
                () -> ExcelMetadataExtractor.extractMetadata(multipartFile));
    }
}
