// Username
driver.findElement(By.xpath("//input[@id='input-user-name']")).sendKeys("sample");

// Password
driver.findElement(By.xpath("//input[@id='input-password-login']")).sendKeys("password");

// Token value
driver.findElement(By.xpath("//input[@id='input-token']")).sendKeys("123456");

// Sign in button
driver.findElement(By.xpath("//button[@id='btn-login-pass']")).click();
