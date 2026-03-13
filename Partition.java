private LocalDate getRunDateSkippingWeekend(LocalDate runDate) {

    long saturdayLogic = getLongProperty(CUSTOM_REAUTH_SATURDAY_LOGIC, 1L);
    long sundayLogic = getLongProperty(CUSTOM_REAUTH_SUNDAY_LOGIC, 1L);

    LocalDate candidateDate = runDate;

    while (true) {

        DayOfWeek day = candidateDate.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY) {

            if (saturdayLogic == 0) {
                return candidateDate;
            }

            candidateDate = candidateDate.plusDays(saturdayLogic);
            continue;
        }

        if (day == DayOfWeek.SUNDAY) {

            if (sundayLogic == 0) {
                return candidateDate;
            }

            candidateDate = candidateDate.plusDays(sundayLogic);
            continue;
        }

        // weekday
        return candidateDate;
    }
}
