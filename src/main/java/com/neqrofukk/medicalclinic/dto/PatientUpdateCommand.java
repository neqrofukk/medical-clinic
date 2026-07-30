package com.neqrofukk.medicalclinic.dto;

import java.time.LocalDate;

public record PatientUpdateCommand(
        String email,
        String idCardNo,
        String firstName,
        String lastName,
        LocalDate birthDay,
        String phoneNumber
) {
}
