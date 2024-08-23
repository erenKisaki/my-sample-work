        String baseUrl = "https://acm.merica.com";
        String projectKey = "LAEKSADFIR";
        String repositorySlug = "bofaopt adapter";
        String filePath = "adapters/BGPA 20DIP 200witch(20Global120Adapter.jaon";
        String branchOrCommit = "refs/heads/master"; // or a specific commit SHA

        String accessToken = "your-access-token";

        // URL encoding the file path to handle spaces and special characters
        String encodedFilePath = java.net.URLEncoder.encode(filePath, StandardCharsets.UTF_8);

        // Construct the request URL
        String requestUrl = String.format("%s/projects/%s/repos/%s/browse/%s?at=%s",
                baseUrl,
                projectKey,
                java.net.URLEncoder.encode(repositorySlug, StandardCharsets.UTF_8),
                encodedFilePath,
                java.net.URLEncoder.encode(branchOrCommit, StandardCharsets.UTF_8));
