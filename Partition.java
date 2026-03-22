public boolean isPencilIconDisplayed() {

    int row = getAvailableRow();

    By dynamicPencilIcon = By.xpath(
        "//*[@id='userform:table01']/tbody/tr[" + row + "]/td[4]/span/a"
    );

    return SupportFunctions.isDisplayed(driver, dynamicPencilIcon);
}

public void clickPencilIconButton() throws Throwable {
    try {

        int row = getAvailableRow();

        By dynamicPencilIcon = By.xpath(
            "//*[@id='userform:table01']/tbody/tr[" + row + "]/td[4]/span/a"
        );

        SupportFunctions.click(driver, dynamicPencilIcon);

        SupportFunctions.logInfoExtentWithScreenShotWithElement(
            "Clicking pencilIcon",
            dynamicPencilIcon
        );

    } catch (Exception e) {
        SupportFunctions.logFailExtentWithScreenShot("Unable to click pencil button");
        throw e;
    }
}
