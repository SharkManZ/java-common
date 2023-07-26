package ru.shark.home.common.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;

import static ru.shark.home.common.common.ErrorConstants.WRONG_DATE_FORMAT;

public class DateUtils {
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

    private DateUtils() {
        // empty constructor
    }

    public static Date parseDate(String value) {
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(String.format(WRONG_DATE_FORMAT, value));
        }
    }
}
