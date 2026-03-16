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
            || StringUtils.isBlank(fallbackClientKey)
            || responseEntity == null
            || responseEntity.getBody() == null) {
        return false;
    }

    JsonObject jsonObject = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject();

    String operation = getJsonValue(jsonObject, "operation");
    String errorMessage = getJsonValue(jsonObject, "error_message");
    String reasonMessage = getJsonValue(jsonObject, "reason_message");

    if (!containsValue(operation, retryEnabledOperations)) {
        return false;
    }

    int statusCode = responseEntity.getStatusCodeValue();

    boolean eligible;

    if (statusCode != 200) {
        eligible = containsValue(errorMessage, errorMessages);
    } else {
        eligible = containsValue(reasonMessage, reasonMessages);
    }

    if (eligible) {
        LOGGER.info(
                "CPX retry triggered for endpointClientKey: {}, status: {}, response: {}",
                endpointClientKey,
                statusCode,
                responseEntity.getBody());
    }

    return eligible;
}
