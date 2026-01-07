private LocalDateTime getNextRun(LocalDateTime previousRun, LocalDateTime currentDate) {

    if (previousRun == null) {
        return currentDate;   // your original first assignment
    }

    LocalDate nextDate = previousRun.toLocalDate().plusDays(1);

    while (isWeekend(nextDate)) {
        nextDate = nextDate.plusDays(1);
    }

    return LocalDateTime.of(nextDate, previousRun.toLocalTime());
}


    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
               date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
