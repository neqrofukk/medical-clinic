package com.neqrofukk.medicalclinic.validators;

import org.springframework.data.domain.Sort;

import java.util.Set;

public final class SortValidator {

    private SortValidator() {
    }

    public static void validate(Sort sort, Set<String> allowedProperties) {
        sort.forEach(order -> {
            if (!allowedProperties.contains(order.getProperty())) {
                throw new IllegalArgumentException(
                        "Nie można posortować po polu '" + order.getProperty() + ". " + "Dozwolone pola: " + allowedProperties);
            }
        });
    }
}
