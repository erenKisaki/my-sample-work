ZoneId zone = ZoneId.systemDefault();

reAuthDate.setTime(
    reAuthDate.toInstant()
        .atZone(zone)
        .toLocalDate()
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
);
