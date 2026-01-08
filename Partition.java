import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleDateUtilTest {

    @InjectMocks
    private ScheduleDateUtil scheduleDateUtil;

    @Mock
    private Environment environment;

    @BeforeEach
    void setup() {
    }

    @Test
    void firstRun_shouldReturnTodayWithResultTime() {
        LocalDateTime now = LocalDateTime.now();
        long resultTime = 1430;

        LocalDateTime result =
                scheduleDateUtil.getNextRun(null, now, resultTime);

        LocalDateTime expected =
                LocalDateTime.of(now.toLocalDate(), LocalTime.of(14, 30));

        assertEquals(expected, result);
    }

    @Test
    void nextRun_shouldWorkForEveryWeekday() {
        LocalDateTime base = LocalDateTime.now();

        for (int i = 0; i < 7; i++) {
            LocalDateTime testDate = base.plusDays(i);
            long resultTime = 900;

            LocalDateTime result =
                    scheduleDateUtil.getNextRun(testDate, testDate, resultTime);

            LocalDate expectedDate = testDate.toLocalDate().plusDays(1);

            if (expectedDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                expectedDate = expectedDate.plusDays(2);
            }
            else if (expectedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                expectedDate = expectedDate.plusDays(1);
            }

            LocalDateTime expected =
                    LocalDateTime.of(expectedDate, LocalTime.of(9, 0));

            assertEquals(expected, result);
        }
    }

    @Test
    void nextRun_shouldSkipWeekend_whenNextDayIsSaturday() {
        LocalDateTime friday = LocalDateTime.now();

        while (friday.getDayOfWeek() != DayOfWeek.FRIDAY) {
            friday = friday.plusDays(1);
        }

        long resultTime = 1000;

        LocalDateTime result =
                scheduleDateUtil.getNextRun(friday, friday, resultTime);

        LocalDateTime expected =
                LocalDateTime.of(friday.toLocalDate().plusDays(3),
                        LocalTime.of(10, 0));

        assertEquals(expected, result);
    }

    @Test
    void nextRun_shouldSkipWeekend_whenNextDayIsSunday() {
        LocalDateTime saturday = LocalDateTime.now();

        while (saturday.getDayOfWeek() != DayOfWeek.SATURDAY) {
            saturday = saturday.plusDays(1);
        }

        long resultTime = 1200;

        LocalDateTime result =
                scheduleDateUtil.getNextRun(saturday, saturday, resultTime);

        LocalDateTime expected =
                LocalDateTime.of(saturday.toLocalDate().plusDays(2),
                        LocalTime.of(12, 0));

        assertEquals(expected, result);
    }
}
