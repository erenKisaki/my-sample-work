private LocalDate resolveDateFromRuleDay(LocalDate baseDate, String ruleDay) {
    if (ruleDay == null || ruleDay.isBlank()) {
        return baseDate;
    }

    DayOfWeek targetDay = DayOfWeek.valueOf(ruleDay.toUpperCase());

    int diff = targetDay.getValue() - baseDate.getDayOfWeek().getValue();
    if (diff < = 0) diff += 7;

    return baseDate.plusDays(diff);
}
