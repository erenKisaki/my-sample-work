List<BatchReauthCustomException> exceptionList =
            BatchPaymentWorkListUtil.getBatchReauthCustomExceptionList();

    exceptionList = exceptionList.stream()
            .sorted(Comparator.comparingLong(BatchReauthCustomException::getTime))
            .collect(Collectors.toList());

    for (BatchReauthCustomException exceptionRule : exceptionList) {

        if (isExceptionDayMatches(exceptionRule, runDate)
                || isExceptionDayOfTheMonthMatches(exceptionRule, runDate)) {

            LocalTime exceptionTime =
                    convertToLocalTime(exceptionRule.getTime());

            // Only evaluate exception at its actual execution time
            if (!runDate.toLocalTime().equals(exceptionTime)) {
                continue;
            }

            // INCLUDE → exception applies
            if (isIncludeErrorCodeMatches(errorCode, exceptionRule)) {
                return true;
            }

            // EXCLUDE → exception blocks this time
            if (isExcludeErrorCodeMatches(exceptionRule.getExcludeError(), errorCode)) {
                return false;
            }

            // Generic exception (no include, no exclude)
            return true;
        }
    }

    // No exception applies at this runDate → eligible
    return true;
