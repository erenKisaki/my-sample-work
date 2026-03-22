public boolean isAuthenticationMechanismDrpDwnDisplayed() {

    int row = getAvailableRow();

    By dynamicDrpDwn = By.xpath(
        "//*[@id='userform:table01']/tbody/tr[" + row + "]/td[3]/select"
    );

    return SupportFunctions.isDisplayed(driver, dynamicDrpDwn);
}

public boolean validateAuthenticationMechanismDrpDwn() throws Throwable {

    int row = getAvailableRow();

    By dynamicDrpDwn = By.xpath(
        "//*[@id='userform:table01']/tbody/tr[" + row + "]/td[3]/select"
    );

    SupportFunctions.click(driver, dynamicDrpDwn);

    SupportFunctions.logInfoExtentWithScreenShotWithElement(
        "Validating values from Action dropdown",
        dynamicDrpDwn
    );

    SupportFunctions.fetchAndValidateDropdownValues(
        driver,
        dynamicDrpDwn,
        propertiesValidationLabelUMS.getProperty("authenticationMechanismDrpDwn1")
    );
    return true;
}

public void selectAuthMechanismDropDownValue(String value) throws Throwable {
    try {

        int row = getAvailableRow();

        By dynamicDrpDwn = By.xpath(
            "//*[@id='userform:table01']/tbody/tr[" + row + "]/td[3]/select"
        );

        SupportFunctions.scrollIntoView(driver, dynamicDrpDwn);

        String dropdownValue = value.contains("OTP Token")
            ? propertiesValidationLabelUMS.getProperty("authenticationMechanismDrpDwn1")
            : null;

        SupportFunctions.selectFromDropdownByVisibleText(
            driver,
            dynamicDrpDwn,
            dropdownValue
        );

        SupportFunctions.logInfoExtentWithScreenShotWithElement(
            "Selecting a value from dropdown",
            dynamicDrpDwn
        );

    } catch (Exception e) {
        SupportFunctions.logFailExtentWithScreenShot(
            "Unable to Select Action value: " + value
        );
        throw e;
    }
}
