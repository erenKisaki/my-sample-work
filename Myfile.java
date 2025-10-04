public static ExtentReports getInstance() throws IOException {
    if (extent == null) {

        FileInputStream fis = new FileInputStream("./src/test/resources/properties/config.properties");
        config.load(fis);

        String env = config.getProperty("env");
        timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        // Your single-file HTML report path (you already had this line)
        String reportPath = System.getProperty("user.dir") + "/ExtentReports/"
                + SupportFunctions.tagNameFetch() + "_" + SupportFunctions.getCurrentDateTime() + ".html";

        // ➜ NEW: ensure parent folder exists
        Path reportFile = Paths.get(reportPath);
        Files.createDirectories(reportFile.getParent());

        // Build reporter with the FILE path (unchanged)
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

        spark.config().setDocumentTitle("Test Results");
        spark.config().setReportName("CIAM-Automation-Report");
        spark.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Tester", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", env);
        extent.setSystemInfo("Release Name", config.getProperty("BuildNumber"));

        // ➜ UPDATED: add a shutdown hook that FLUSHES first, then injects CSS/JS
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (extent != null) {
                    extent.flush();                // <<< this is the crucial bit
                }
                injectCustomCSSAndJS(reportPath);   // now the file exists, safe to patch it
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
    return extent;
}


public static void flushReport() {
    if (extent != null) {
        extent.flush();
    }
}
@AfterSuite
public void afterSuite() {
    ExtentReporterManager.flushReport();
}
@io.cucumber.java.AfterAll
public static void afterAll() {
    ExtentReporterManager.flushReport();
}
