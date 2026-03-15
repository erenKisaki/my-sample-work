public boolean shouldRetry() {

    if (responseEntity == null || responseEntity.getBody() == null) {
        return false;
    }

    int statusCode = responseEntity.getStatusCodeValue();

    JsonObject jsonObject = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject();

    String operation = jsonObject.has("operation")
            ? jsonObject.get("operation").getAsString()
            : null;

    String errorMessage = jsonObject.has("error_message")
            ? jsonObject.get("error_message").getAsString()
            : null;

    String reasonMessage = jsonObject.has("reason_message")
            ? jsonObject.get("reason_message").getAsString()
            : null;

    if (!containsValue(operation, operations)) {
        return false;
    }

    if (statusCode != 200) {
        return containsValue(errorMessage, errorMessages);
    }

    if (statusCode == 200) {
        return containsValue(reasonMessage, reasonMessages);
    }

    return false;
}

private boolean containsValue(String value, String[] configValues) {

    if (value == null || configValues == null) {
        return false;
    }

    return Arrays.stream(configValues)
            .anyMatch(config -> config.equalsIgnoreCase(value.trim()));
}
