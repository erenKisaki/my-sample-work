LocalDateTime earliestExceptionDate = null;

for (BatchRetryCustomException exceptionRule : exceptionList) {

    LocalDateTime candidateException = null;

    // -------- Rule 1: Error code + custom day rule --------
    if (StringUtils.isNotEmpty(exceptionRule.getIncludeError())
            && StringUtils.isNotEmpty(errorCode)
            && exceptionRule.getIncludeError().equalsIgnoreCase(errorCode)) {

        LocalDate fixedDate =
                resolveDateFromRuleDay(runDate.toLocalDate(), exceptionRule.getDay());

        candidateException = LocalDateTime.of(
                fixedDate,
                convertToLocalTime(exceptionRule.getTime())
        );
    }

    // -------- Rule 2: Day / Nth day / error fallback rules --------
    else if (
            isExceptionDayMatches(exceptionRule, runDate)
            || isExceptionDayOfTheMonthMatches(exceptionRule, runDate)
            || isErrorCodeMatches(exceptionRule.getExcludeError(), errorCode)
    ) {
        candidateException = LocalDateTime.of(
                runDate.toLocalDate(),
                convertToLocalTime(exceptionRule.getTime())
        );
    }

    // -------- Collect earliest valid exception --------
    if (candidateException != null && candidateException.isAfter(nowDate)) {

        if (earliestExceptionDate == null
                || candidateException.isBefore(earliestExceptionDate)) {

            earliestExceptionDate = candidateException;
        }
    }
}
