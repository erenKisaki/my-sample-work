public static boolean validateDropdownOptionsByLabelMap(Select dropdown,
                                                        Properties propertiesValidationLabelUMS) {
    try {
        // Convert properties values to lowercase Set for fast lookup
        Set<String> propertyValues = propertiesValidationLabelUMS.values().stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Check all dropdown options exist in properties
        return dropdown.getOptions().stream()
                .map(option -> option.getText().trim().toLowerCase())
                .allMatch(propertyValues::contains);

    } catch (Exception e) {
        return false;
    }
}
