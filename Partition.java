ZoneId estZone = ZoneId.of("America/New_York");

var currentDateTime= scheduledDate.toInstant()
        .atZone(estZone)
        .toLocalDateTime();
