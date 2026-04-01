if (StringUtils.isNotBlank(errorMessage)) {

    var retryConfigurationMessageList =
            configuration.getStringList(CpxConstants.CPX_RETRY_ERROR_MESSAGE_KEY + clientKey);

    // If no config → do not retry
    if (CollectionUtils.isEmpty(retryConfigurationMessageList)) {
        return false;
    }

    boolean isRetryConfigurationMessage = retryConfigurationMessageList.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .anyMatch(k ->
                    Optional.ofNullable(errorMessage)
                            .map(String::toLowerCase)
                            .orElse("")
                            .contains(k.toLowerCase())
            );

    // If errorMessage exists but doesn't match any configured values → return false
    if (!isRetryConfigurationMessage) {
        return false;
    }
}
