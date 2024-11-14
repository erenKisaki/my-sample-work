
package com.bofa.pftool.connection.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class BitbucketRestClientExample {

    public static void main(String[] args) {
        String baseUrl = "https://sch.bon.bankofamerica.com/rest/api/latest";
        String projectKey = "IAEXSADPTR";
        String repositorySlug = "bofoapt_adapter";
        String branchOrCommit = "refs/heads/master";  // or a specific branch name
        String accessToken = "<YOUR_ACCESS_TOKEN>";
        String filePath = "<LOCAL_PATH_TO_ZIP_FILE>"; // Path to the local zip file you want to upload

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // URL encoding for the repository slug and branch name
            String encodedRepositorySlug = java.net.URLEncoder.encode(repositorySlug, "UTF-8");
            String encodedBranchOrCommit = java.net.URLEncoder.encode(branchOrCommit, "UTF-8");

            // Construct the request URL
            String requestUrl = String.format("%s/projects/%s/repos/%s/browse?at=%s",
                    baseUrl, projectKey, encodedRepositorySlug, encodedBranchOrCommit);

            // Set up HTTP POST request for file upload
            HttpPost uploadFile = new HttpPost(requestUrl);
            uploadFile.addHeader("Authorization", "Bearer " + accessToken);

            // Load the file to be uploaded
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            
            // Build the multipart entity with the file data
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", fileInputStream, org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM, file.getName())
                    .addTextBody("message", "Uploading zip file to branch " + branchOrCommit) // Commit message
                    .addTextBody("branch", branchOrCommit) // Specify the target branch
                    .build();
            
            uploadFile.setEntity(entity);

            // Execute the request
            HttpResponse response = httpClient.execute(uploadFile);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200 || statusCode == 201) {
                System.out.println("File uploaded successfully.");
            } else {
                System.out.println("Failed to upload file. HTTP error code: " + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
