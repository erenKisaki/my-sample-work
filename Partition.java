By FORGOT_PASSWORD =
    By.xpath("//a[contains(@href,'forgot-password')]//span[normalize-space()='Forgot Password?']");

By FORGOT_USERNAME =
    By.xpath("//a[contains(@href,'forgot-username')]//span[normalize-space()='Forgot Username?']");
By FP_HEADING = By.xpath("//h1[normalize-space()='Forgot Password']");
By FP_DESCRIPTION = By.xpath("//div[contains(@class,'forgotPassword-description')]//p");
By FP_USERNAME = By.xpath("//input[@id='RPLUsername']");
By FP_EMAIL = By.xpath("//input[@id='RPLEmail']");
By FP_USERNAME_ERROR = By.xpath("//div[@id='errorUsername']");
By FP_USERNAME_INFO = By.xpath("//div[@id='usernameInfoRPL']");
By SEND_RESET_LINK = By.xpath("//button[@id='btnSendResetLink']");
By CANCEL_RESET = By.xpath("//a[contains(@class,'btn-secondary') and normalize-space()='Cancel']");
