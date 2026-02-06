
private int getStatusByReAuthAttempt(int value) {
    Map<String, Integer> statusMap =
            BatchPaymentWorkListUtil.getRecurringStatusMap();

    if (statusMap == null || statusMap.isEmpty()) {
        return 99;
    }

    return statusMap.entrySet()
            .stream()
            .filter(entry -> isExactReauthMatch(entry.getKey(), value))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(99);
}

private boolean isExactReauthMatch(String key, int value) {
    if (StringUtils.isBlank(key)) {
        return false;
    }

    int underscoreIndex = key.lastIndexOf('_');
    if (underscoreIndex < 0 || underscoreIndex == key.length() - 1) {
        return false;
    }

    try {
        int attempt = Integer.parseInt(key.substring(underscoreIndex + 1));
        return attempt == value;
    } catch (NumberFormatException ex) {
        return false;
    }
}
