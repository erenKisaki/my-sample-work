@Component
public class ScheduleDateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleDateUtil.class);

    @Autowired
    private Environment environment;

    // =====================================================
    // ENTRY POINT
    // =====================================================
    public LocalDateTime getEligibleNextRun(LocalDateTime currentDate,
                                            long retryCount,
                                            List<BatchReauthCustomException> exceptionList) {

        // 1️⃣ Evaluate current date
        RunEvaluation today = evaluateRun(currentDate, retryCount, exceptionList);

        if (today.exceptionMatched) {
            return today.runDate;
        }

        // 2️⃣ Move to next business day first
        LocalDate nextBusinessDay =
                getRunDateSkippingWeekends(currentDate.toLocalDate().plusDays(1));

        LocalDateTime candidateBase =
                LocalDateTime.of(nextBusinessDay, today.runDate.toLocalTime());

        // 3️⃣ Evaluate candidate date
        RunEvaluation candidate = evaluateRun(candidateBase, retryCount, exceptionList);

        return candidate.runDate;
    }

    // =====================================================
    // CORE ENGINE
    // =====================================================
    private RunEvaluation evaluateRun(LocalDateTime baseDate,
                                      long retryCount,
                                      List<BatchReauthCustomException> exceptionList) {

        long oddTime  = getLongProperty("CATDPAY_REAUTH_ODD_TIME",  1000L);
        long evenTime = getLongProperty("CATDPAY_REAUTH_EVEN_TIME", 1800L);

        long selectedTime = (retryCount % 2 == 0) ? evenTime : oddTime;

        LocalDateTime run = LocalDateTime.of(
                baseDate.toLocalDate(),
                convertToLocalTime(selectedTime)
        );

        boolean exceptionMatched = false;

        for (BatchReauthCustomException rule : exceptionList) {
            if (isExceptionDayMatches(rule, baseDate)
             || isExceptionDayOfMonthMatches(rule, baseDate)) {

                exceptionMatched = true;

                LocalDateTime exceptionTime = LocalDateTime.of(
                        baseDate.toLocalDate(),
                        convertToLocalTime(rule.getTime())
                );

                if (exceptionTime.isAfter(run)) {
                    run = exceptionTime;
                }
            }
        }

        return new RunEvaluation(run, exceptionMatched);
    }

    // =====================================================
    // WEEKEND HANDLING
    // =====================================================
    private LocalDate getRunDateSkippingWeekends(LocalDate runDate) {

        long saturdayLogic = getLongProperty("CATDPAY_REAUTH_SATURDAY_LOGIC", 2L);
        long sundayLogic   = getLongProperty("CATDPAY_REAUTH_SUNDAY_LOGIC",   1L);

        if (runDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return runDate.plusDays(saturdayLogic);
        }

        if (runDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return runDate.plusDays(sundayLogic);
        }

        return runDate;
    }

    // =====================================================
    // MATCH HELPERS
    // =====================================================
    private boolean isExceptionDayMatches(BatchReauthCustomException rule, LocalDateTime date) {
        return rule.getDay() != null &&
               date.getDayOfWeek().name().equalsIgnoreCase(rule.getDay());
    }

    private boolean isExceptionDayOfMonthMatches(BatchReauthCustomException rule, LocalDateTime date) {
        return rule.getDayOfMonth() != null &&
               rule.getDayOfMonth().equals(date.getDayOfMonth());
    }

    // =====================================================
    // UTILITIES
    // =====================================================
    private long getLongProperty(String key, long defaultValue) {
        String value = environment.getProperty(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid numeric value for property: " + key, e);
        }
    }

    private LocalTime convertToLocalTime(long time) {
        int hour   = (int) (time / 100);
        int minute = (int) (time % 100);
        return LocalTime.of(hour, minute);
    }


}
