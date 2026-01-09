var fixedRuleDate = resolveDateFromRuleDay(runDate.toLocalDate(), exceptionRule.getDay());
exceptionDate = LocalDateTime.of(fixedRuleDate, convertToLocalTime(exceptionRule.getTime()));
