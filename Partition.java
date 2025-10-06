package hooks;

import com.aventstack.extentreports.ExtentTest;
import extentReport.ExtentReporterManager;
import extentReport.ExtentTestManager;
import io.cucumber.java.*;

// Keep your existing base class if you need config/driver fields
public class hooks extends TestManager {

    public static ExtentTest feature;
    public static String tagname;

    @BeforeAll
    public static void initExtentOnce() throws Exception {
        // Ensure singleton is created before any scenario
        ExtentTestManager.extent();
        System.out.println("[Extent] init via @BeforeAll");
    }

    @Before
    public void setupSuite(Scenario scenario) throws Throwable {
        // Your existing setup calls:
        BackGroundClass.loadPropertiesFromFile("./src/test/resources/properties/config.properties", config);

        String scenarioName = scenario.getName();

        // Create Extent test for this scenario and bind to current thread
        ExtentTest test = ExtentTestManager.startTest(scenarioName, "Scenario Execution");

        // Put Cucumber tags as categories
        test.assignCategory(scenario.getSourceTagNames().toArray(new String[0]));

        // Your existing custom setup
        LoadJsonUserData();
    }

    @After(order = 1)
    public void tearDown() throws Exception {
        // <<< IMPORTANT: do NOT flush here (was causing empty/partial reports)
        // Keep your cleanup here (examples below are from your screenshot):

        RestAssured.reset();

        try (FileWriter file = new FileWriter(UserDataManagementJSON)) {
            file.write(userProfileDetails.toString());
        }

        if (driver != null) {
            String browser = config.getProperty("Browser").toLowerCase();
            driver.quit();
            switch (browser) {
                case "chrome" -> {
                    SupportFunctions.killChromeBrowser(config.getProperty("OS"));
                    System.out.println("Killing Chrome Driver");
                }
                case "safari" -> System.out.println("Killing Safari Driver");
                case "edge" -> System.out.println("Killing Edge Driver");
                default -> { /* no-op */ }
            }
        }

        // Unbind this thread's test
        ExtentTestManager.endTest();
    }

    @AfterAll
    public static void flushOnceAtSuiteEnd() {
        // One true flush after all scenarios complete
        System.out.println("[Extent] tests=" +
                ExtentTestManager.extent().getReport().getTestList().size());
        ExtentReporterManager.flushReport();
    }
}


package extentReport;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> TL = new InheritableThreadLocal<>();
    private static final ExtentReports EXTENT;

    static {
        try {
            EXTENT = ExtentReporterManager.getInstance();
            System.out.println("[Extent] instance@" + System.identityHashCode(EXTENT));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ExtentTestManager() {}

    public static ExtentReports extent() { return EXTENT; }

    public static ExtentTest startTest(String name, String description) {
        ExtentTest t = EXTENT.createTest(name, description);
        TL.set(t);
        return t;
    }

    public static ExtentTest getTest() { return TL.get(); }

    public static void endTest() { TL.remove(); }
}


package extentReport;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public final class ExtentReporterManager {

    private static volatile ExtentReports extent;
    public static String reportPath;                 // full path to the HTML file
    private static final Properties config = new Properties();

    private ExtentReporterManager() {}

    public static ExtentReports getInstance() throws IOException {
        if (extent == null) {
            synchronized (ExtentReporterManager.class) {
                if (extent == null) {
                    init();
                }
            }
        }
        return extent;
    }

    private static void init() throws IOException {
        // Optional config (env, build etc.)
        try (FileInputStream fis =
                new FileInputStream("./src/test/resources/properties/config.properties")) {
            config.load(fis);
        } catch (IOException ignore) { /* optional */ }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String tag = sanitize(System.getProperty("extent.tag", "IST-Reports"));

        Path dir = Paths.get(System.getProperty("user.dir"), "ExtentReports");
        Files.createDirectories(dir);
        reportPath = dir.resolve(tag + "_" + timestamp + ".html").toString();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Test Results");
        spark.config().setReportName("CIAM-Automation-Report");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Tester", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", config.getProperty("env", "local"));
        extent.setSystemInfo("Release Name", config.getProperty("BuildNumber", "NA"));

        // Safety net: if JVM exits unexpectedly
        Runtime.getRuntime().addShutdownHook(new Thread(ExtentReporterManager::flushReport));
    }

    private static String sanitize(String s) {
        return (s == null || s.isBlank()) ? "Report" : s.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    /** Call once after all scenarios (Cucumber @AfterAll). */
    public static void flushReport() {
        if (extent == null) return;
        // Ensure Spark materializes even if no tests (edge cases)
        if (extent.getReport() == null || extent.getReport().getTestList().isEmpty()) {
            extent.createTest("Bootstrap").info("Forces report materialization.");
        }
        extent.flush();
        System.out.println("[Extent] Flushed -> " + reportPath);
    }
}
