package com.neqrofukk.medicalclinic.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.springframework.util.StringUtils.hasText;

public final class Utils {

    private Utils() {
    }

    public static void setIfPresent(String val, Consumer<String> setter) {
        if (hasText(val)) { // not null, length > 0, not only whitespaces
            setter.accept(val);
        }
    }

    public static void setIfNotNullDate(LocalDate val, Consumer<LocalDate> setter) {
        if (val != null) {
            setter.accept(val);
        }
    }

    public static void setIfNotNullDateTime(LocalDateTime val, Consumer<LocalDateTime> setter) {
        if (val != null) {
            setter.accept(val);
        }
    }

    public static void setIfPositiveNumber(Integer val, Consumer<Integer> setter) {
        if (val != null && val > 0) {
            setter.accept(val);
        }
    }

}
