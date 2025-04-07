ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3DataDobiBucket);
ListObjectsV2Result result;

do {
    result = s3Client.listObjectsV2(req);

    for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
        keys.add(objectSummary.getKey());
        LOGGER.logInfo("All objects retrieved: " + objectSummary.getKey());
    }

    // If there's more to retrieve, set the continuation token
    req.setContinuationToken(result.getNextContinuationToken());

} while (result.isTruncated());
