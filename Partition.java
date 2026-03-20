public void selectApplicationDropDownValue(String value) throws Throwable {
    try {

        String drpdownValue = switch (value) {
            case "SAA" -> propertiesValidationLabelUMS.getProperty("global.label.application.SAA");
            case "BESS" -> propertiesValidationLabelUMS.getProperty("global.label.application.BESS");
            case "VMWARE_ADMIN" -> propertiesValidationLabelUMS.getProperty("global.label.application.VMWARE_ADMIN");
            case "BESS_TANDEM_ADMIN" -> propertiesValidationLabelUMS.getProperty("global.label.application.BESS_TANDEM_ADMIN");
            case "OTHERS" -> propertiesValidationLabelUMS.getProperty("global.label.application.OTHERS");
            case "GTX" -> propertiesValidationLabelUMS.getProperty("global.label.application.GTX");
            default -> null;
        };

        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='userform:table01']/tbody/tr"));

        for (int i = 1; i <= rows.size(); i++) {

            // ✅ Check if row already has Application value (SAA etc.)
            List<WebElement> appText = driver.findElements(
                By.xpath("//*[@id='userform:table01']/tbody/tr[" + i + "]/td[1][not(select)]")
            );

            // 👉 If td has text (means already filled row) → skip
            if (!appText.isEmpty() && !appText.get(0).getText().trim().isEmpty()) {
                continue;
            }

            // ✅ Now find dropdown in that row
            By dynamicDropdown = By.xpath("//*[@id='userform:table01']/tbody/tr[" + i + "]/td[1]/select");

            List<WebElement> dropdowns = driver.findElements(dynamicDropdown);

            if (!dropdowns.isEmpty()) {

                SupportFunctions.scrollIntoView(driver, dynamicDropdown);
                SupportFunctions.selectFromDropdownByVisibleText(driver, dynamicDropdown, drpdownValue);

                return; // ✅ stop after first empty row
            }
        }

        throw new RuntimeException("No empty row found to select dropdown");

    } catch (Exception e) {
        SupportFunctions.logFailExtentWithScreenShot("Unable to Select Action value: " + value);
        throw e;
    }
}
