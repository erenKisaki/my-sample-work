import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BitbucketRestClientExample {

    public static void main(String[] args) {
        String baseUrl = "https://bitbucket.example.com";
        String projectKey = "PROJECT_KEY";
        String repositorySlug = "repository-slug";
        String filePath = "path/to/your/file.txt";
        String branchOrCommit = "refs/heads/master"; // or a specific commit SHA

        String accessToken = "your-access-token";
        String requestUrl = baseUrl + "/rest/api/1.0/projects/" + projectKey + "/repos/" + repositorySlug + "/raw/" + filePath + "?at=" + branchOrCommit;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(requestUrl);
            request.addHeader("Authorization", "Bearer " + accessToken);

            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) { // HTTP 200 OK
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String content = reader.lines().collect(Collectors.joining("\n"));
                    System.out.println("File content:\n" + content);
                }
            } else {
                System.out.println("Failed to retrieve file content. HTTP error code: " + statusCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
