LocalDateTime ldt = LocalDateTime.ofInstant(
            calendar.toInstant(),
            calendar.getTimeZone().toZoneId()
    );

var currentDateExecution =  LocalDateTime.parse(ldt.format(ORACLE_FORMAT), ORACLE_FORMAT);
