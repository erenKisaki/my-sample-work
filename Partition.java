private boolean isAnyExceptionRuleMatches(LocalDateTime runDate,
                                          LocalDateTime scheduleDate,
                                          String errorCode) {

	var nextScheduleDate = runDate;

    List<BatchRetryCustomException> retryCustomExceptionList =
            BatchPaymentWorkflowUtil.getActiveCustomExceptionList();
			

    if (CollectionUtils.isNotEmpty(retryCustomExceptionList)) {

        for (BatchRetryCustomException exceptionRule : retryCustomExceptionList) {
		
			if (StringUtils.isNotEmpty(exceptionRule.getIncludeError()) && StringUtils.isNotEmpty(errorCode)
				&& exceptionRule.getIncludeError().equalsIgnoreCase(errorCode)) {
				var base = runDate.toLocalDate();

				var fixedRuleDate = resolveDateFromRuleDay(base, exceptionRule.getDay()); // get the Day
				nextScheduleDate = LocalDateTime.of(fixedRuleDate, convertToLocalTime(exceptionRule.getTime()));
				
			} else if ((isExceptionDayMatches(exceptionRule, runDate)
                    && isExceptionDayOfTheMonthMatches(exceptionRule, runDate))
                    && !isErrorCodeMatches(exceptionRule.getExcludeError(), errorCode)) {

                nextScheduleDate =
                        LocalDateTime.of(runDate.toLocalDate(),
                                convertToLocalTime(exceptionRule.getTime()));
                
            }
			
			if (nextScheduleDate.isAfter(scheduleDate)) {

                    runDate = nextScheduleDate;

                    LOGGER.info("Eligible run date found matching exception rule: {} for run date: {}",
                            exceptionRule, runDate);

                    return true;
             }
        }
    }

    return false;
}


private LocalDate resolveDateFromRuleDay(LocalDate baseDate, String ruleDay) {
    if (ruleDay == null || ruleDay.isBlank()) {
        return baseDate;
    }

    DayOfWeek targetDay = DayOfWeek.valueOf(ruleDay.toUpperCase());

    int diff = targetDay.getValue() - baseDate.getDayOfWeek().getValue();
    if (diff < = 0) diff += 7;

    return baseDate.plusDays(diff);
}
