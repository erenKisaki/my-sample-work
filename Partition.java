var fixedRuleDate = resolveDateFromRuleDay(runDate.toLocalDate(), exceptionRule.getDay());
				nextScheduleDate = LocalDateTime.of(fixedRuleDate, convertToLocalTime(exceptionRule.getTime()));
