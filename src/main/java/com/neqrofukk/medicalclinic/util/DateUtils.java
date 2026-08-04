package com.neqrofukk.medicalclinic.util;

import java.time.LocalDateTime;

public final class DateUtils {

    private DateUtils() {}

    boolean isOverlapOpenEnd(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
