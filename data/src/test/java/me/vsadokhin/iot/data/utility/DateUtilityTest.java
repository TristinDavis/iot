package me.vsadokhin.iot.data.utility;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilityTest {

    @Test
    public void getDayStart() {
        // setup
        long now = System.currentTimeMillis();

        // act
        long result = DateUtility.getDayStart(now);

        // verify
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        assertThat(result, is(calendar.getTimeInMillis()));
    }

    @Test
    public void getWeekStart() {
        // setup
        Instant instant = LocalDate.of(2018, 9, 29).atStartOfDay().toInstant(ZoneOffset.UTC);

        // act
        long result = DateUtility.getWeekStart(instant.toEpochMilli());
        
        // verify
        assertThat(result, is(ZonedDateTime.of(2018,9,24,0,0,0,0,ZoneId.of("UTC")).toInstant().toEpochMilli()));
    }
}