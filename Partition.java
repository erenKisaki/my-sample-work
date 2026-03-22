public int getAvailableRow() {

    List<WebElement> rows = driver.findElements(
        By.xpath("//*[@id='userform:table01']/tbody/tr")
    );

    for (int i = 1; i <= rows.size(); i++) {

        // ✅ Row is available if dropdown exists
        List<WebElement> dropdown = driver.findElements(
            By.xpath("//*[@id='userform:table01']/tbody/tr[" + i + "]/td[1]/select")
        );

        if (!dropdown.isEmpty()) {
            return i;
        }
    }

    throw new RuntimeException("No available row found");
}

public void enterLocalUID() throws Throwable {
    try {

        int row = getAvailableRow(); // ✅ SAME logic

        String localUID = config.getProperty("NewSID");

        By inputField = By.xpath(
            "//*[@id='userform:table01']/tbody/tr[" + row + "]/td[2]/input"
        );

        SupportFunctions.clear(driver, inputField);
        SupportFunctions.enterText(driver, localUID, inputField);

        SupportFunctions.logInfoExtentWithScreenShotWithElement(
            "Entering LocalUID - " + localUID,
            inputField
        );

    } catch (Exception e) {
        SupportFunctions.logFailExtentWithScreenShot("Unable to enter LocalUID");
        throw e;
    }
}

public boolean validateLocalUIDTextField() {

    int row = getAvailableRow();

    By inputField = By.xpath(
        "//*[@id='userform:table01']/tbody/tr[" + row + "]/td[2]/input"
    );

    String value = driver.findElement(inputField).getAttribute("value");

    return value == null || value.trim().isEmpty();
}
