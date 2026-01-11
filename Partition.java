var candidate = runDate;

    while (true) {

        DayOfWeek day = candidate.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY) {
            candidate = candidate.plusDays(saturdayLogic);
            continue;
        }

        if (day == DayOfWeek.SUNDAY) {
            candidate = candidate.plusDays(sundayLogic);
            continue;
        }

        // guaranteed weekday
        return candidate;
    }
