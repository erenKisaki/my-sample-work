import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class BitbucketJsonReader {
    public static void main(String[] args) throws Exception {
        String username = "<yourusername>";
        String apppassword = "<yourAppPassword>";
        String fileUrl = "https://api.bitbucket.org/2.0/repositories/APP.json";

        byte[] encodedAuth = Base64.getEncoder().encode((username + ":" + apppassword).getBytes());

        // Establishing connection to the given file URL
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setRequestProperty("Authorization", "Basic " + new String(encodedAuth));

        // Reading the JSON content from the file
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonContent.append(line);
        }
        reader.close();
        connection.disconnect();

        // The entire JSON content as a string
        String jsonString = jsonContent.toString();

        // Output the JSON content (optional)
        System.out.println(jsonString);
    }
}
