public class BulkUploadUsingExcel {
    private static Logger logger = LoggerFactory.getLogger(BulkUploadUsingExcel.class);

    public static void main(String[] args) {
        logger.info("Starting Utility.....");
        try {
            String curlData = getCurlAsString();
            Map<String, List<String>> dataMap = readDataFromExcel("C:\\Users\\ZKIAPMO.CORP\\Documents\\Excel Data Read\\SampleTestData.xlsx");
            for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
                String selector = entry.getKey();
                List<String> connectionIdList = entry.getValue();
                callServiceUsingCurl(curlData, selector, connectionIdList);
                logger.info(selector + ": " + connectionIdList);
            }
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

    public static Map<String, List<String>> readDataFromExcel(String path) {
        Map<String, List<String>> dataMap = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(path)) {
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                if (currentRow.getRowNum() == 0) {
                    continue; // Skip header row
                }
                Cell selectorCell = currentRow.getCell(0);
                Cell connectionIdCell = currentRow.getCell(1);
                if (selectorCell != null && connectionIdCell != null) {
                    String selector = selectorCell.getStringCellValue();
                    String connectionId = connectionIdCell.getStringCellValue();
                    if (!dataMap.containsKey(selector)) {
                        dataMap.put(selector, new ArrayList<>());
                    }
                    dataMap.get(selector).add(connectionId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataMap;
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
			
			
			List<List<String>> partitionedConnList = getPartitionedList(connectionIdList, 100);
			for(List<String> connectIdList: partitionedConnList) {
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
				jsonData = modifyJsonData(jsonData, connectIdList, selector);
				connection.getOutputStream().write(jsonData.getBytes());
				int responseCode = connection.getResponseCode();
				logger.info("The response code: " + responseCode);
			}
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
        Pattern regex = Pattern.compile("('[^']')|(\"[^\"]\")|(\\S+)");
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

	private static List<List<String>> getPartitionedList(List<String> origList, int partitionSize) {
		List<List<String>> partitions = new ArrayList<>();
		for (int i = 0; i < origList.size(); i += partitionSize) {
			partitions.add(origList.subList(i, Math.min(i + partitionSize, origList.size())));
		}
		return partitions;
	}
}
