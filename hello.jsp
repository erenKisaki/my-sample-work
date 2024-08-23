String encodedRepositorySlug = URLEncoder.encode(repositorySlug, "UTF-8");
            String encodedFilePath = URLEncoder.encode(filePath, "UTF-8");
            String encodedBranchOrCommit = URLEncoder.encode(branchOrCommit, "UTF-8");

            // Construct the request URL
            String requestUrl = String.format("%s/projects/%s/repos/%s/browse/%s?at=%s",
                    baseUrl,
                    projectKey,
                    encodedRepositorySlug,
                    encodedFilePath,
                    encodedBranchOrCommit);
