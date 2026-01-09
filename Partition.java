for (BatchRetryCustomException exceptionRule : retryCustomExceptionList) {
		
			if (StringUtils.isNotEmpty(exceptionRule.getIncludeError()) && StringUtils.isNotEmpty(errorCode)
				&& exceptionRule.getIncludeError().equalsIgnoreCase(errorCode)) {
				var base = runDate.toLocalDate();

				var fixedRuleDate = resolveDateFromRuleDay(base, exceptionRule.getDay());
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
