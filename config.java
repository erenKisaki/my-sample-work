package com.chase.digital.payments.wires.util;

import com.chase.digital.payments.wires.model.ProductRequestBody;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.CustomProperties;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ExcelMetadataExtractor {

    public ProductRequestBody extractMetadata(final MultipartFile multipartFile) throws IOException {
        Objects.requireNonNull(multipartFile, "multipartFile must not be null");

        try (InputStream in = multipartFile.getInputStream();
             OPCPackage pkg = OPCPackage.open(in);
             XSSFWorkbook wb = new XSSFWorkbook(pkg)) {

            POIXMLProperties props = wb.getProperties();
            CustomProperties custom = props.getCustomProperties();

            ProductRequestBody dto = new ProductRequestBody();

            dto.setPfId(getAsString(custom, "pfId"));
            dto.setSessionId(getAsString(custom, "sessionId"));
            dto.setEci(getAsString(custom, "eci"));
            dto.setFileId(getAsString(custom, "fileId"));

            // Use custom property fileName if available, otherwise original file name
            String fileName = getAsString(custom, "fileName");
            dto.setFileName(fileName != null ? fileName : multipartFile.getOriginalFilename());

            dto.setTotalRecords(getAsString(custom, "totalRecords"));
            dto.setValidRecords(getAsString(custom, "validRecords"));
            dto.setInvalidRecords(getAsString(custom, "invalidRecords"));
            dto.setUpdateId(getAsString(custom, "updateId"));

            String partial = getAsString(custom, "isPartiallyProcessed");
            dto.setPartiallyProcessed(parseBoolean(partial));

            dto.setErrorMessage(getAsString(custom, "errorMessage"));
            dto.setCreusId(getAsString(custom, "creusId"));

            return dto;

        } catch (InvalidFormatException e) {
            throw new IOException("Invalid Excel format: only .xlsx is supported", e);
        }
    }

    private static String getAsString(CustomProperties custom, String name) {
        if (custom == null || name == null) return null;
        CTProperty p = custom.getProperty(name);
        if (p == null) return null;

        if (p.isSetLpwstr()) return p.getLpwstr();
        if (p.isSetLpstr())  return p.getLpstr();
        if (p.isSetI4())     return Integer.toString(p.getI4());
        if (p.isSetI8())     return Long.toString(p.getI8());
        if (p.isSetR8())     return Double.toString(p.getR8());
        if (p.isSetDecimal()) return p.getDecimal().toString();
        if (p.isSetBool())   return Boolean.toString(p.getBool());
        if (p.isSetDate())   return p.getDate().toString();
        if (p.isSetFiletime()) return p.getFiletime().toString();
        return null;
    }

    private static boolean parseBoolean(String value) {
        if (value == null) return false;
        String v = value.trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("yes");
    }
}
