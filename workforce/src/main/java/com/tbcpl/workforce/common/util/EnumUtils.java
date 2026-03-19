package com.tbcpl.workforce.common.util;

public class EnumUtils {

    public static <T extends Enum<T>> T parse(Class<T> enumClass, String value, T defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
