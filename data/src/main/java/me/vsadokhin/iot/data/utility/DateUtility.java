package me.vsadokhin.iot.data.utility;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public final class DateUtility {

    private DateUtility(){}

    public static long getDayStart(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).truncatedTo(ChronoUnit.DAYS).toEpochMilli();
    }

    public static long getWeekStart(long milliseconds) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK);
        calendar.setTimeInMillis(milliseconds);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTimeInMillis();
    }
}
