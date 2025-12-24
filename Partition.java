			
		DateTimeFormatter ORACLE_FORMAT =
        DateTimeFormatter.ofPattern("dd-MMM-yy hh.mm.ss.nnnnnnnnn a", Locale.ENGLISH);

        LocalDateTime lastExecution =
            LocalDateTime.parse(lastExecutionStr, ORACLE_FORMAT);

        LocalDate candidateDate = lastExecution.toLocalDate().plusDays(1);

        while (candidateDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
               candidateDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            candidateDate = candidateDate.plusDays(1);
        }

        List<LocalDateTime> matches = new ArrayList<>();

        for (ScheduleRule rule : rules) {

            if (!rule.active) continue;

            boolean match = false;

            if (rule.dateOfMonth != null &&
                    rule.dateOfMonth == candidateDate.getDayOfMonth())
                match = true;

            if (rule.dayOfWeek != null &&
                    rule.dayOfWeek == candidateDate.getDayOfWeek())
                match = true;

            if (match) {
                matches.add(LocalDateTime.of(candidateDate, rule.time));
            }
        }

        LocalDateTime nextExecution;

        if (!matches.isEmpty()) {
            nextExecution = matches.stream()
                    .min(LocalDateTime::compareTo)
                    .get();
        } else {
            nextExecution = LocalDateTime.of(
                    candidateDate,
                    lastExecution.toLocalTime()
            );
        }

        return nextExecution.format(OUTPUT_FORMAT);
