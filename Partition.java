String message = errorMessage.toLowerCase();

boolean isRetryConfigurationMessage = retryConfigurationMessageList.stream()
    .filter(Objects::nonNull)
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .anyMatch(k -> message.contains(k.toLowerCase()));
