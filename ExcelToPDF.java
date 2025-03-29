@When("the user selects {string} from the Application dropdown")
public void theUserSelectsFromTheApplicationDropdown(String application) {
    WebElement applicationDropdown = driver.findElement(By.name("application")); // Updated based on UI reference
    Select dropdown = new Select(applicationDropdown);
    dropdown.selectByVisibleText(application);
}
