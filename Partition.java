public boolean isEligibleForRetry(ResponseEntity<String> responseEntity) {

    boolean retryEnabled =
            configuration.getBoolean(CPX_INTERNAL_RETRY_ENABLEMENT_KEY + endpointClientKey);

    String[] retryEnabledOperations =
            configuration.getStringArray(CPX_INTERNAL_RETRY_ENABLEMENT_KEY + endpointClientKey
                    + CPX_INTERNAL_RETRY_CHASE_OPERATIONS);

    String[] errorMessages =
            configuration.getStringArray(CPX_INTERNAL_RETRY_ENABLEMENT_KEY + endpointClientKey
                    + CPX_INTERNAL_RETRY_CHASE_ERROR_MESSAGES);

    String[] reasonMessages =
            configuration.getStringArray(CPX_INTERNAL_RETRY_ENABLEMENT_KEY + endpointClientKey
                    + CPX_INTERNAL_RETRY_CHASE_REASON_MESSAGES);

    String fallbackClientKey =
            configuration.getString(CPX_INTERNAL_RETRY_ENABLEMENT_KEY + endpointClientKey
                    + CPX_INTERNAL_RETRY_CHASE_FALLBACK);

    if (!retryEnabled
            || ArrayUtils.isEmpty(retryEnabledOperations)
            || ArrayUtils.isEmpty(errorMessages)
            || ArrayUtils.isEmpty(reasonMessages)
            || StringUtils.isBlank(fallbackClientKey)) {
        return false;
    }

    if (responseEntity == null || responseEntity.getBody() == null) {
        return false;
    }

    JsonObject jsonObject = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject();

    String operation = getJsonValue(jsonObject, "operation");
    String errorMessage = getJsonValue(jsonObject, "error_message");
    String reasonMessage = getJsonValue(jsonObject, "reason_message");

    boolean eligible =
            containsValue(operation, retryEnabledOperations)
                    && containsValue(errorMessage, errorMessages)
                    && containsValue(reasonMessage, reasonMessages);

    if (eligible) {
        LOGGER.info("CPX retry is enabled for endpointClientKey: {} and errorMessage: {}",
                endpointClientKey, responseEntity.getBody());
    }

    return eligible;
}

private String getJsonValue(JsonObject jsonObject, String key) {
    return jsonObject.has(key) && !jsonObject.get(key).isJsonNull()
            ? jsonObject.get(key).getAsString()
            : null;
}
