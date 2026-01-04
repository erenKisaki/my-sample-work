private LocalDateTime getNextDateTimeForDay(String dayName, LocalTime ruleTime) {
    DayOfWeek targetDay = DayOfWeek.valueOf(dayName.toUpperCase());
    LocalDate today = LocalDate.now();
    LocalDate nextDate = today.with(TemporalAdjusters.nextOrSame(targetDay));
    return LocalDateTime.of(nextDate, ruleTime);
}

// it should be added in else part

LocalTime ruleTime = convertToLocalTime(rule.getTime());

    LocalDateTime ruleDateTime = getNextDateTimeForDay(rule.getDay(), ruleTime);

    if (ruleDateTime.isBefore(dateExecution)) {
        ruleDateTime = ruleDateTime.plusWeeks(1);
    }

    ruleMatchedDateTimes.add(ruleDateTime);
