import com.atlassian.bitbucket.server.rest.client.BitbucketClient;
import com.atlassian.bitbucket.server.rest.client.api.RepositoryApi;
import com.atlassian.bitbucket.server.rest.client.model.HttpAuthentication;
import com.atlassian.bitbucket.server.rest.client.model.HttpBearerAuthentication;
import com.atlassian.bitbucket.server.rest.client.model.Repository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class BitbucketExample {
    public static void main(String[] args) {
        // Replace with your Bitbucket base URL
        String bitbucketBaseUrl = "https://bitbucket.example.com";

        // Replace with your HTTP access token
        String accessToken = "your-access-token";

        // Create a Bitbucket client using the access token
        HttpAuthentication authentication = new HttpBearerAuthentication(accessToken);
        BitbucketClient bitbucketClient = new BitbucketClient(bitbucketBaseUrl, authentication);

        // Define repository details
        String projectKey = "PROJECT_KEY";
        String repositorySlug = "repository-slug";
        String filePath = "path/to/your/file.txt";
        String branchOrCommit = "master"; // or a specific commit SHA

        // Access the repository API
        RepositoryApi repositoryApi = bitbucketClient.api().repositories();

        // Get the repository
        Repository repository = repositoryApi.get(projectKey, repositorySlug);

        // Get file content as InputStream
        try (InputStream fileContent = repositoryApi.fileContent(repository, branchOrCommit, filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, StandardCharsets.UTF_8))) {
            
            // Convert InputStream to String using BufferedReader
            String content = reader.lines().collect(Collectors.joining("\n"));
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
