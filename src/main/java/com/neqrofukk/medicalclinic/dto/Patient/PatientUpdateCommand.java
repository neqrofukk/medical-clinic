package com.neqrofukk.medicalclinic.dto.Patient;

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
