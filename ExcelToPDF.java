while (true) {
    s3ObjectSummaries.addAll(objectListing.getObjectSummaries());
    if (objectListing.isTruncated()) {
        objectListing = s3Client.listNextBatchOfObjects(objectListing);
    } else {
        break;
    }
}
