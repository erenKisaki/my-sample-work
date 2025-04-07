package com.example;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;

import java.util.HashMap;
import java.util.Map;

public class ProcessS3PageHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String BUCKET_NAME = "your-cross-account-bucket";
    private static final String ROLE_ARN = "arn:aws:iam::<AccountB-ID>:role/CrossAccountS3AccessRole";
    private static final String REGION = "us-east-1"; // Change as needed

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        Map<String, Object> response = new HashMap<>();

        try {
            // Step 1: Assume the cross-account role
            AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                    .withRegion(REGION)
                    .build();

            AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
                    .withRoleArn(ROLE_ARN)
                    .withRoleSessionName("S3PageSession");

            AssumeRoleResult assumeRoleResult = stsClient.assumeRole(assumeRoleRequest);
            Credentials creds = assumeRoleResult.getCredentials();

            BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                    creds.getAccessKeyId(),
                    creds.getSecretAccessKey(),
                    creds.getSessionToken()
            );

            // Step 2: Create S3 client with assumed credentials
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                    .withRegion(REGION)
                    .build();

            // Step 3: Prepare the ListObjectsV2Request
            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName(BUCKET_NAME)
                    .withMaxKeys(1000);

            if (input.containsKey("continuationToken")) {
                request.setContinuationToken((String) input.get("continuationToken"));
            }

            // Step 4: List objects and process them
            ListObjectsV2Result result = s3Client.listObjectsV2(request);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                String key = objectSummary.getKey();
                logger.log("Processing object: " + key);

                // Optional: Access metadata or add filter logic here
                // ObjectMetadata metadata = s3Client.getObjectMetadata(BUCKET_NAME, key);
            }

            // Step 5: Return pagination details
            response.put("isTruncated", result.isTruncated());
            response.put("nextContinuationToken", result.getNextContinuationToken());

        } catch (Exception e) {
            logger.log("ERROR: " + e.getMessage());
            throw new RuntimeException("Lambda execution failed", e);
        }

        return response;
    }
}
