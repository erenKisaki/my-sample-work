import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;

public class BitbucketRestClientExample {

    public static void main(String[] args) {
        String baseUrl = "https://scm.horizon.bankofamerica.com/rest/api/latest";
        String projectKey = "IAEXSADPTR";
        String repositorySlug = "bofaopt_adapter";
        String branchOrCommit = "refs/heads/master"; // or a specific commit SHA
        String accessToken = ""; // Add your token here
        
        // Destination path for saving the downloaded ZIP file
        String destinationPath = "C:/path/to/local/folder/adapter.zip"; // Change this to your desired folder

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // URL encode branch/commit for safety
            String encodedBranchOrCommit = branchOrCommit.replace(" ", "%20");

            // Construct the request URL for ZIP download
            String requestUrl = String.format(
                "%s/projects/%s/repos/%s/archive?format=zip&at=%s",
                baseUrl, projectKey, repositorySlug, encodedBranchOrCommit
            );

            // Set up HTTP GET request
            HttpGet request = new HttpGet(requestUrl);
            request.addHeader("Authorization", "Bearer " + accessToken);

            // Execute the request
            HttpResponse response = httpClient.execute(request);

            // Check the response status code
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) { // HTTP 200 OK
                // Stream the response content to a file
                try (InputStream inputStream = response.getEntity().getContent();
                     FileOutputStream outputStream = new FileOutputStream(new File(destinationPath))) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    System.out.println("File downloaded successfully to: " + destinationPath);
                }
            } else {
                System.out.println("Failed to download file. HTTP error code: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
