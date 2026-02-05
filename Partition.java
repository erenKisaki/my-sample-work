// Retrieve comma-separated patterns as List (reference-style)
        var tokenRegexPatterns =
                configuration.getStringList("wallet.token.patterns");

        if (tokenRegexPatterns.isEmpty()) {
            return false;
        }

        // Stream-based validation
        boolean isTokenRegexMatches =  Stream.of(token)
                .allMatch(StringUtils::isNotBlank)
                && tokenRegexPatterns.stream()
                        .filter(StringUtils::isNotBlank)
                        .map(Pattern::compile)
                        .anyMatch(pattern ->
                                pattern.matcher(token).find());
