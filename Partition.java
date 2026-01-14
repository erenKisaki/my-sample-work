@Test
    public void testMondayNextRunDate() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 12, 11, 00); // Monday

        assertTrue(LocalDateTime.of(2026, 1, 13, 10, 00)
                .equals(scheduledDateAdapter.getEligibleNextRun(input, input, 1)));
    }

    @Test
    public void testTuesdayNextRunDate() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 13, 11, 00); // Tuesday

        assertTrue(LocalDateTime.of(2026, 1, 14, 18, 00)
                .equals(scheduledDateAdapter.getEligibleNextRun(input, input, 2)));
    }

    @Test
    public void testWednesdayNextRunDate() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 14, 11, 00); // Wednesday

        assertTrue(LocalDateTime.of(2026, 1, 15, 10, 00)
                .equals(scheduledDateAdapter.getEligibleNextRun(input, input, 3)));
    }

    @Test
    public void testThursdayNextRunDate() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 15, 11, 00); // Thursday

        assertTrue(LocalDateTime.of(2026, 1, 16, 18, 00)
                .equals(scheduledDateAdapter.getEligibleNextRun(input, input, 4)));
    }

    @Test
    public void testFridayNextRunDateSkipsWeekend() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 16, 11, 00); // Friday

        assertTrue(LocalDateTime.of(2026, 1, 19, 10, 00)
                .equals(scheduledDateAdapter.getEligibleNextRun(input, input, 5)));
    }

    @Test
    public void testSaturdayNextRunDateSkipsToMonday() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 17, 11, 00); // Saturday

        assertTrue(LocalDateTime.of(2026, 1, 19, 18, 00)
                .equals(scheduledDateAdapter.getEligibleNextRun(input, input, 6)));
    }

    @Test
    public void testSundayNextRunDateSkipsToMonday() {
        LocalDateTime input = LocalDateTime.of(2026, 1, 18, 11, 00); // Sunday

        assertTrue(LocalDateTime.of(2026, 1, 19, 10, 00)
                .equals(scheduledDateAdapter.getEligibleNextRun(input, input, 7)));
    }
