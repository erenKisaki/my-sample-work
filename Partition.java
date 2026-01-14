@Test
    @DisplayName("When runDate is null and requestCount is odd -> return same scheduled date with odd time")
    void testRunDateNullAndOddCount() {

        LocalDateTime scheduledDate = LocalDateTime.of(2026, 1, 12, 9, 0); // Monday
        long requestCount = 1; // odd

        LocalDateTime result =
                paymentService.getNextRun(null, scheduledDate, requestCount);

        LocalDateTime expected =
                LocalDateTime.of(LocalDate.of(2026, 1, 12), LocalTime.of(11, 0));

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("When runDate is null and requestCount is even -> return same scheduled date with even time")
    void testRunDateNullAndEvenCount() {

        LocalDateTime scheduledDate = LocalDateTime.of(2026, 1, 12, 9, 0);
        long requestCount = 2; // even
		
        LocalDateTime result =
                paymentService.getNextRun(null, scheduledDate, requestCount);

        LocalDateTime expected =
                LocalDateTime.of(LocalDate.of(2026, 1, 12), LocalTime.of(14, 0));

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("When runDate exists and next day is weekday -> return next business day")
    void testNextBusinessDayWeekday() {

        LocalDateTime runDate = LocalDateTime.of(2026, 1, 12, 10, 0); // Monday
        LocalDateTime scheduledDate = LocalDateTime.of(2026, 1, 12, 10, 0);
        long requestCount = 1;


        LocalDateTime result =
                paymentService.getNextRun(runDate, scheduledDate, requestCount);

        LocalDateTime expected =
                LocalDateTime.of(LocalDate.of(2026, 1, 13), LocalTime.of(9, 0));

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("When next day is Saturday -> skip to Monday")
    void testWeekendSkipFromFriday() {

        LocalDateTime runDate = LocalDateTime.of(2026, 1, 9, 10, 0); // Friday
        LocalDateTime scheduledDate = LocalDateTime.of(2026, 1, 9, 10, 0);
        long requestCount = 2;

        LocalDateTime result =
                paymentService.getNextRun(runDate, scheduledDate, requestCount);

        LocalDateTime expected =
                LocalDateTime.of(LocalDate.of(2026, 1, 12), LocalTime.of(15, 0)); // Monday

        assertEquals(expected, result);
    }
