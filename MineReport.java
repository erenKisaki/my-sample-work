    reportFile = java.nio.file.Paths.get(
        System.getProperty("user.dir"), "ExtentReports", env + "-" + ts + ".html");

    try {
      java.nio.file.Files.createDirectories(reportFile.getParent());  // <-- ensure folder
    } catch (java.io.IOException e) {
      throw new RuntimeException("Cannot create dir: " + reportFile.getParent(), e);
    }
