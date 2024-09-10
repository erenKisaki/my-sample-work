import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public List<String> readSingleColumnData(String fileLocation, String inputType) throws IOException {
        List<String> columnData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(fileLocation));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = null;

            if (inputType.equalsIgnoreCase("SAML")) {
                sheet = workbook.getSheetAt(0);
            } else if (inputType.equalsIgnoreCase("PingAccess")) {
                sheet = workbook.getSheetAt(1);
            } else if (inputType.equalsIgnoreCase("OIDC")) {
                sheet = workbook.getSheetAt(2);
            }

            if (sheet == null) {
                return columnData; // Return empty list if no sheet is found
            }

            // Iterate over all rows in the sheet and read the first column
            for (Row row : sheet) {
                Cell cell = row.getCell(0); // Assumes there is only one column at index 0
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            columnData.add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            columnData.add(String.valueOf(cell.getNumericCellValue()));
                            break;
                        default:
                            columnData.add("");  // Handle other types as empty strings
                    }
                }
            }
        }
        return columnData;
    }
}
