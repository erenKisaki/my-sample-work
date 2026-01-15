@Test
public void testNextRunForAllDays_EvenTimeOnly() {

    assertTrue(LocalDateTime.of(2026, 1, 13, 18, 0)
            .equals(scheduleDateAdapter.getNextRun(LocalDateTime.of(2026, 1, 12, 10, 0), 2))); // Mon → Tue

    assertTrue(LocalDateTime.of(2026, 1, 14, 18, 0)
            .equals(scheduleDateAdapter.getNextRun(LocalDateTime.of(2026, 1, 13, 10, 0), 2))); // Tue → Wed

    assertTrue(LocalDateTime.of(2026, 1, 15, 18, 0)
            .equals(scheduleDateAdapter.getNextRun(LocalDateTime.of(2026, 1, 14, 10, 0), 2))); // Wed → Thu

    assertTrue(LocalDateTime.of(2026, 1, 16, 18, 0)
            .equals(scheduleDateAdapter.getNextRun(LocalDateTime.of(2026, 1, 15, 10, 0), 2))); // Thu → Fri

    assertTrue(LocalDateTime.of(2026, 1, 19, 18, 0)
            .equals(scheduleDateAdapter.getNextRun(LocalDateTime.of(2026, 1, 16, 10, 0), 2))); // Fri → Mon

    assertTrue(LocalDateTime.of(2026, 1, 19, 18, 0)
            .equals(scheduleDateAdapter.getNextRun(LocalDateTime.of(2026, 1, 17, 10, 0), 2))); // Sat → Mon

    assertTrue(LocalDateTime.of(2026, 1, 19, 18, 0)
            .equals(scheduleDateAdapter.getNextRun(LocalDateTime.of(2026, 1, 18, 10, 0), 2))); // Sun → Mon
}
