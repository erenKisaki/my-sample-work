ZoneId estZone = ZoneId.of("America/New_York");

Date scheduledDate = paymentProcessing.getScheduledDate();

LocalDateTime currentDateTime= scheduledDate.toInstant()
        .atZone(estZone)
        .toLocalDateTime();
