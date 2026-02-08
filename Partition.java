var tokenRegexPatterns =
        Optional.ofNullable(configuration)
            .map(c -> c.getStringList("app.xbucks.token.regex"))
            .orElse(Collections.emptyList());
