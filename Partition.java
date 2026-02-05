boolean isTokenMatchesRegex =
        Stream.of(tokenRegex, token)
              .allMatch(StringUtils::isNotBlank)
        && Stream.of(token)
                 .anyMatch(t -> t.matches(tokenRegex));
