public static boolean isValuePresentInProperties(String input,
                                                 Properties propertiesValidationLabelUMS) {
    if (input == null || propertiesValidationLabelUMS == null) {
        return false;
    }

    String normalizedInput = input.trim().toLowerCase();

    return propertiesValidationLabelUMS.values().stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .map(String::trim)
            .map(String::toLowerCase)
            .anyMatch(val -> val.equals(normalizedInput));
}
