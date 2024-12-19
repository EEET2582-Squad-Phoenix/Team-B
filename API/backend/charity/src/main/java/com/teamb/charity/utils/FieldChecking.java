package com.teamb.charity.utils;

import java.math.BigDecimal;
import java.util.Objects;

public class FieldChecking {

    public static boolean isNullOrEmpty(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

    public static boolean isPositive(BigDecimal value) {
        return !Objects.isNull(value) && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isNegative(BigDecimal value) {
        return !Objects.isNull(value) && value.compareTo(BigDecimal.ZERO) < 0;
    }

}
