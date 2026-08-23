package com.neqrofukk.medicalclinic.validators;

import com.neqrofukk.medicalclinic.exceptions.InvalidSortPropertyException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SortValidator {
    public static void validate(Sort sort, Set<String> allowedProperties) {
        sort.forEach(order -> {
            if (!allowedProperties.contains(order.getProperty())) {
                throw new InvalidSortPropertyException(order.getProperty(), allowedProperties);
            }
        });
    }
}
