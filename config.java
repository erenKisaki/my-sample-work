import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

	public String getProperty(String propertyName) {
		String propertyValue = null;
	    Properties properties = new Properties();
		try (InputStream input = ConfigProperties.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
			propertyValue = properties.getProperty(propertyName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		return propertyValue;
	}
}
