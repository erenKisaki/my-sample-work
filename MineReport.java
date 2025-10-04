package your.pkg.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ExtentReporterManager {

    private static volatile ExtentReports extent;
    private static final Object LOCK = new Object();

    private static String timestamp;
    private static String reportBaseDir;   // folder containing the report
    private static String reportHtmlPath;  // .../index.html actually written
    private static final Properties config = new Properties();

    private ExtentReporterManager() {}

    /** Get (or create) the singleton ExtentReports instance. */
    public static ExtentReports getInstance() {
        if (extent == null) {
            synchronized (LOCK) {
                if (extent == null) {
                    try {
                        loadConfig();
                        setupPaths();
                        extent = buildExtent(reportHtmlPath);
                        addShutdownHook();
                    } catch (IOException ioe) {
                        throw new RuntimeException("Failed to initialise ExtentReports", ioe);
                    }
                }
            }
        }
        return extent;
    }

    /** Call this once at the very end (listener/hook does this for you). */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    /** Optional: zip the whole report directory (HTML, CSS/JS, assets) to the given zip file. */
    public static void compressReportFolder(String zipFilePath) throws IOException {
        if (reportBaseDir == null) return;
        Path base = Paths.get(reportBaseDir);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            Files.walk(base)
                 .filter(Files::isRegularFile)
                 .forEach(p -> {
                     String rel = base.relativize(p).toString().replace("\\", "/");
                     try (InputStream in = Files.newInputStream(p)) {
                         zos.putNextEntry(new ZipEntry(rel));
                         byte[] buf = new byte[4096];
                         int len;
                         while ((len = in.read(buf)) > 0) {
                             zos.write(buf, 0, len);
                         }
                         zos.closeEntry();
                     } catch (IOException e) {
                         throw new UncheckedIOException(e);
                     }
                 });
        }
    }

    // ---------- internals ----------

    private static void loadConfig() throws IOException {
        // Load from classpath: src/test/resources/properties/config.properties
        try (InputStream is = ExtentReporterManager.class
                .getClassLoader()
                .getResourceAsStream("properties/config.properties")) {
            if (is != null) {
                config.load(is);
            }
        }
    }

    private static void setupPaths() throws IOException {
        timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        // Example: ExtentReports/Smoke_2025-10-04_20-10-22/
        String tag = safe(config.getProperty("tag"),
                SupportFunctions.tagNameFetch()); // your helper; falls back to config "tag"
        reportBaseDir = Paths.get(System.getProperty("user.dir"),
                "ExtentReports", tag + "_" + timestamp).toString();
        Files.createDirectories(Paths.get(reportBaseDir));
        reportHtmlPath = Paths.get(reportBaseDir, "index.html").toString(); // always "index.html"
    }

    private static ExtentReports buildExtent(String htmlPath) {
        ExtentSparkReporter spark = new ExtentSparkReporter(htmlPath);
        spark.config().setDocumentTitle("Test Results");
        spark.config().setReportName("CIAM-Automation-Report");
        spark.config().setTheme(Theme.DARK);

        ExtentReports er = new ExtentReports();
        er.attachReporter(spark);

        // System info
        String env = safe(config.getProperty("env"), "local");
        er.setSystemInfo("OS", System.getProperty("os.name"));
        er.setSystemInfo("Tester", System.getProperty("user.name"));
        er.setSystemInfo("Environment", env);
        er.setSystemInfo("Release Name", safe(config.getProperty("BuildNumber"), "NA"));

        return er;
    }

    private static void addShutdownHook() {
        // Ensure flush happens even if runner forgets, then inject CSS/JS if you use it.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                flush();
                injectCustomCSSAndJS(reportBaseDir); // no-op if you don’t have custom files
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * If you have files src/test/resources/report/custom.css or custom.js,
     * they’ll be copied next to index.html and linked into it.
     */
    private static void injectCustomCSSAndJS(String dir) throws IOException {
        Path base = Paths.get(dir);
        Path html = base.resolve("index.html");
        if (!Files.exists(html)) return;

        // Copy optional resources
        Path cssSrc = getResourceToFile("report/custom.css", base.resolve("custom.css"));
        Path jsSrc  = getResourceToFile("report/custom.js",  base.resolve("custom.js"));

        if (cssSrc == null && jsSrc == null) return;

        String htmlText = Files.readString(html, StandardCharsets.UTF_8);
        StringBuilder inject = new StringBuilder();
        if (cssSrc != null) inject.append("<link rel=\"stylesheet\" href=\"custom.css\" />\n");
        if (jsSrc  != null) inject.append("<script src=\"custom.js\"></script>\n");

        // Insert before </head>
        String patched = htmlText.replace("</head>", inject.toString() + "</head>");
        Files.writeString(html, patched, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static Path getResourceToFile(String resourcePath, Path dest) throws IOException {
        try (InputStream is = ExtentReporterManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
            return dest;
        }
    }

    public static String getReportBaseDir() {
        return reportBaseDir;
    }

    public static String getReportHtmlPath() {
        return reportHtmlPath;
    }

    private static String safe(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }
}
