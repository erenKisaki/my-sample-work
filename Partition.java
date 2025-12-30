private long getLongProperty(String key, long defaultValue) {
    String value = environment.getProperty(key);

    if (value == null || value.isBlank()) {
        return defaultValue;
    }

    try {
        return Long.parseLong(value.trim());
    } catch (NumberFormatException e) {
        throw new IllegalStateException("Invalid numeric value for property: " + key, e);
    }
}
