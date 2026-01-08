@Test
public void nextRun_shouldSkipWeekend_whenNextDayIsSaturday() {

    LocalDateTime base = LocalDateTime.now().withSecond(0).withNano(0);

    // force base so that next day becomes Saturday
    while (base.plusDays(1).getDayOfWeek() != DayOfWeek.SATURDAY) {
        base = base.plusDays(1);
    }

    long resultTime = 1000;

    LocalDateTime result =
            scheduleDateUtil.getNextRun(base, base, resultTime);

    LocalDate expectedDate = base.toLocalDate().plusDays(1); // this is Saturday

    // apply real production logic
    if (expectedDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
        expectedDate = expectedDate.plusDays(2);
    }

    assertEquals(expectedDate, result.toLocalDate());
    assertEquals(10, result.getHour());
    assertEquals(0, result.getMinute());
}


@Test
public void nextRun_shouldWorkForEveryWeekday() {

    LocalDateTime base = LocalDateTime.now().withSecond(0).withNano(0);

    for (int i = 0; i < 7; i++) {

        LocalDateTime testDate = base.plusDays(i);
        long resultTime = 900;

        LocalDateTime result =
                scheduleDateUtil.getNextRun(testDate, testDate, resultTime);

        // mimic real production logic
        LocalDate expectedDate = testDate.toLocalDate().plusDays(1);

        if (expectedDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            expectedDate = expectedDate.plusDays(2);
        }
        else if (expectedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            expectedDate = expectedDate.plusDays(1);
        }

        assertEquals(expectedDate, result.toLocalDate());
        assertEquals(9, result.getHour());
        assertEquals(0, result.getMinute());
    }
}
