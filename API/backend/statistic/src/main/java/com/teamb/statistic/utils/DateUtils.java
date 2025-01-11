package com.teamb.statistic.utils;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

public class InstantUtils {

    public static Instant floor(Instant date, TemporalUnit unit) {
        return date.truncatedTo(unit);
    }

    public static Instant ceil(Instant date, TemporalUnit unit) {
        return date.plus(1, unit).truncatedTo(unit);
    }
}
