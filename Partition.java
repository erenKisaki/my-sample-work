import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BulkUploadUsingExcel {
    private static Logger logger = LoggerFactory.getLogger(BulkUploadUsingExcel.class);

    public static void main(String[] args) {
        logger.info("Starting Utility.....");
        try {
            String curlData = getCurlAsString();
            Map<String, List<String>> samlDataMap = new HashMap<>();
            Map<String, List<String>> oauthDataMap = new HashMap<>();
            Map<String, List<String>> pingAccessDataMap = new HashMap<>();
            
            readDataFromExcel("C:\\Users\\ZKIAPMO.CORP\\Documents\\Excel Data Read\\SampleTestData.xlsx", samlDataMap, oauthDataMap, pingAccessDataMap);
            
            processData(curlData, samlDataMap, "SAML");
            processData(curlData, oauthDataMap, "OAUTH");
            processData(curlData, pingAccessDataMap, "PingAccess");
            
        } catch (Exception e) {
            logger.debug("SelectorError: Error executing the script. Message: " + e.getMessage(), e);
            logger.error("SelectorError: Error executing the script. Message: " + e.getMessage());
        }
        logger.info("Utility ended.....");
    }

    public static String getCurlAsString() throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("CurlData.txt")) {
            if (is == null)
                return null;
            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    public static void readDataFromExcel(String path, Map<String, List<String>> samlDataMap, Map<String, List<String>> oauthDataMap, Map<String, List<String>> pingAccessDataMap) {
        try (FileInputStream fis = new FileInputStream(path)) {
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheet("Authtype");
            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                if (currentRow.getRowNum() == 0) {
                    continue; // Skip header row
                }
                Cell selectorCell = currentRow.getCell(0);
                Cell connectionIdCell = currentRow.getCell(1);
                Cell authTypeCell = currentRow.getCell(2);
                if (selectorCell != null && connectionIdCell != null && authTypeCell != null) {
                    String selector = selectorCell.getStringCellValue();
                    String connectionId = connectionIdCell.getStringCellValue();
                    String authType = authTypeCell.getStringCellValue();
                    
                    switch (authType) {
                        case "SAML":
                            samlDataMap.computeIfAbsent(selector, k -> new ArrayList<>()).add(connectionId);
                            break;
                        case "OAUTH":
                            oauthDataMap.computeIfAbsent(selector, k -> new ArrayList<>()).add(connectionId);
                            break;
                        case "PingAccess":
                            pingAccessDataMap.computeIfAbsent(selector, k -> new ArrayList<>()).add(connectionId);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processData(String curlData, Map<String, List<String>> dataMap, String authType) {
        for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
            String selector = entry.getKey();
            List<String> connectionIdList = entry.getValue();
            callServiceUsingCurl(curlData, selector, connectionIdList);
            logger.info(authType + " - " + selector + ": " + connectionIdList);
        }
    }

    public static boolean callServiceUsingCurl(String curlCommand, String selector, List<String> connectionIdList) {
        try {
            List<String> matchList = splitCurl(curlCommand);
            String[] parts = matchList.toArray(new String[0]);
            String reqMethod = parts[2];
            String url = parts[4].replaceAll("'", "").replace("SAMLPassthroughConnSetSel", selector);
            StringBuilder headers = new StringBuilder();
            String jsonData = "";
            for (int i = 6; i < parts.length; i++) {
                if (parts[i].startsWith("-H")) {
                    headers.append(parts[i + 1].replaceAll("\"", "").replaceAll("\n", "")).append("\n");
                    i++;
                } else if (parts[i].startsWith("-d")) {
                    jsonData = parts[i + 1].replaceAll("\"", "");
                    i++;
                }
            }
            URL urlobj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlobj.openConnection();
            connection.setRequestMethod(reqMethod);
            connection.setDoOutput(true);
            // Setting headers
            String[] headerParts = headers.toString().split("\n");
            for (String header : headerParts) {
                String[] keyValue = header.split(": ");
                connection.setRequestProperty(keyValue[0], keyValue[1]);
            }
            // Forming the Request body JSON Object
            jsonData = modifyJsonData(jsonData, connectionIdList, selector);
            connection.getOutputStream().write(jsonData.getBytes());
            int responseCode = connection.getResponseCode();
            logger.info("The response code: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String modifyJsonData(String jsonData, List<String> connectionIds, String selector)
            throws Exception {
        StringBuilder connectionsJson = new StringBuilder();
        for (String connectionId : connectionIds) {
            connectionsJson.append("[\"fields\": [[\"name\": \"Connection\", \"value\": \"").append(connectionId)
                    .append("\"]], \"defaultRow\": false},");
        }
        if (connectionsJson.length() > 0) {
            connectionsJson.setLength(connectionsJson.length() - 1); // Remove trailing comma
        }
        jsonData = jsonData.replace("\"SAMLPassthroughConnSetSel\"", "\"" + selector + "\"").replaceAll("\n", "")
                .replaceAll("\r", "");
        JSONObject reqBodyJson = new JSONObject(jsonData);
        if (reqBodyJson.has("configuration")) {
            JSONObject configJson = reqBodyJson.getJSONObject("configuration");
            String configData = configJson.toString();
            configData = configData.replace("\"rows\": []", "\"rows\": [" + connectionsJson.toString() + "]");
            reqBodyJson.put("configuration", new JSONObject(configData));
        }
        return reqBodyJson.toString();
    }

    private static List<String> splitCurl(String curlCommand) {
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("('[^']*')|(\"[^\"]*\")|(\\S+)");
        Matcher regexMatcher = regex.matcher(curlCommand);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                matchList.add(regexMatcher.group(2));
            } else {
                matchList.add(regexMatcher.group());
            }
        }
        return matchList;
    }
}
