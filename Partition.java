private boolean isAnyExceptionRuleMatches(LocalDateTime runDate, String errorCode) {

    List<BatchReauthCustomException> exceptionList =
            BatchPaymentWorkListUtil.getBatchReauthCustomExceptionList();

    // 🔹 Sort exception rules by time (ascending)
    exceptionList = exceptionList.stream()
            .sorted(Comparator.comparingLong(BatchReauthCustomException::getTime))
            .collect(Collectors.toList());

    for (BatchReauthCustomException exceptionRule : exceptionList) {

        if (isExceptionDayMatches(exceptionRule, runDate)
                || isExceptionDayOfTheMonthMatches(exceptionRule, runDate)) {

            if (StringUtils.isNotEmpty(exceptionRule.getIncludeError())
                    && StringUtils.isNotEmpty(errorCode)
                    && exceptionRule.getIncludeError().contains(errorCode)
                    && runDate.toLocalTime()
                               .equals(convertToLocalTime(exceptionRule.getTime()))) {
                return true;
            }
        }
        else if (!isErrorCodeMatches(exceptionRule.getExcludeError(), errorCode)) {
            return true;
        }
    }
    return false;
}
