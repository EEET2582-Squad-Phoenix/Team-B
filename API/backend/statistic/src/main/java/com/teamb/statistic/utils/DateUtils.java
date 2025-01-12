package com.teamb.statistic.utils;

import java.time.temporal.TemporalUnit;
import java.util.Date;

public class DateUtils {

    public static Date floor(Date date, TemporalUnit unit) {
        return Date.from(date.toInstant().truncatedTo(unit));
    }

    public static Date ceil(Date date, TemporalUnit unit) {
        return Date.from(date.toInstant().plus(1, unit).truncatedTo(unit));
    }
}
