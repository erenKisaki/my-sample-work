private int getStatusByReAuthAttempt(int value) {
    Map<String, Integer> recurringPaymentStatusMap =
            BatchPaymentWorkListUtil.getRecurringStatusMap();

    if (recurringPaymentStatusMap == null || recurringPaymentStatusMap.isEmpty()) {
        return 99;
    }

    String suffix = String.valueOf(value);

    return recurringPaymentStatusMap.entrySet()
            .stream()
            .filter(entry -> StringUtils.endsWith(entry.getKey(), suffix))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(99);
}
