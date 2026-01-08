public LocalDateTime getEligibleNextRun(LocalDateTime currentDate,
                                        LocalDateTime scheduleDate,
                                        PaymentProcessing paymentProcessing,
                                        long resultTime) {

    boolean isEligibleRunFound = false;
    LocalDateTime runDate = null;

    // Track first attempt and second attempt
    int attempts = 0;
    LocalDateTime fallbackNextDay = null;

    while (!isEligibleRunFound) {

        runDate = getNextRun(runDate, currentDate, resultTime);

        // Save "plus one day" as fallback
        if (attempts == 0) {
            fallbackNextDay = runDate;
        }

        if (isAnyExceptionRuleMatches(runDate,
                                      scheduleDate,
                                      paymentProcessing.getErrorCode())) {
            isEligibleRunFound = true;
        }

        attempts++;

        // After checking current date & plus one day → apply default
        if (!isEligibleRunFound && attempts >= 2) {
            return fallbackNextDay;
        }
    }

    return runDate;
}


