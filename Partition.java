@Test
public void testNextRunForAllDays_EvenTimeOnly() {

    LocalDateTime[] inputs = {
            LocalDateTime.of(2026, 1, 12, 9, 0), // Monday
            LocalDateTime.of(2026, 1, 13, 9, 0), // Tuesday
            LocalDateTime.of(2026, 1, 14, 9, 0), // Wednesday
            LocalDateTime.of(2026, 1, 15, 9, 0), // Thursday
            LocalDateTime.of(2026, 1, 16, 9, 0), // Friday
            LocalDateTime.of(2026, 1, 17, 9, 0), // Saturday
            LocalDateTime.of(2026, 1, 18, 9, 0)  // Sunday
    };

    LocalDateTime[] expectedResults = {
            LocalDateTime.of(2026, 1, 13, 18, 0), // Mon → Tue
            LocalDateTime.of(2026, 1, 14, 18, 0), // Tue → Wed
            LocalDateTime.of(2026, 1, 15, 18, 0), // Wed → Thu
            LocalDateTime.of(2026, 1, 16, 18, 0), // Thu → Fri
            LocalDateTime.of(2026, 1, 19, 18, 0), // Fri → Mon
            LocalDateTime.of(2026, 1, 19, 18, 0), // Sat → Mon
            LocalDateTime.of(2026, 1, 19, 18, 0)  // Sun → Mon
    };

    for (int i = 0; i < inputs.length; i++) {
        LocalDateTime actual = scheduledDateAdapter.getNextRun(inputs[i], 2);

        assertTrue(expectedResults[i].equals(actual));
    }
}
