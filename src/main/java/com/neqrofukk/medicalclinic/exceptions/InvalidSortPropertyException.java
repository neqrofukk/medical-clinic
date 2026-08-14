package com.neqrofukk.medicalclinic.exceptions;

import com.neqrofukk.medicalclinic.exceptions.handler.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class InvalidSortPropertyException extends MedicalClinicException {
    public InvalidSortPropertyException(String property, java.util.Set<String> allowed) {
        super("Nie można posortować po polu '" + property + "'. Dozwolone pola: " + allowed,
                HttpStatus.BAD_REQUEST);
    }
}