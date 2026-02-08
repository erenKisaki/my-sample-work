var tokenRegexPatterns =
        Optional.ofNullable(configuration)
            .map(c -> configuration.getStringList("app.xbucks.token.regex"))
            .orElse(Collections.emptyList());
