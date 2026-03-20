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

        for (int i = 2; i <= rows.size(); i++) {

            By dynamicDropdown = By.xpath("//*[@id='userform:table01']/tbody/tr[" + i + "]/td[1]/select");

            WebElement dropdown = driver.findElement(dynamicDropdown);
            Select select = new Select(dropdown);

            String selectedText = select.getFirstSelectedOption().getText();

            if (selectedText.equalsIgnoreCase("Please Select")) {

                // 🔥 reuse your existing methods
                SupportFunctions.scrollIntoView(driver, dynamicDropdown);
                SupportFunctions.selectFromDropdownByVisibleText(driver, dynamicDropdown, drpdownValue);

                return; // stop after selecting first empty row
            }
        }

        throw new RuntimeException("No empty dropdown available");

    } catch (Exception e) {
        SupportFunctions.logFailExtentWithScreenShot("Unable to Select Action value: " + value);
    }
}
