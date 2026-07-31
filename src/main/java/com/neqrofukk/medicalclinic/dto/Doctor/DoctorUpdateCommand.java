package com.neqrofukk.medicalclinic.dto.Doctor;

public record DoctorUpdateCommand(
        String email,
        String firstName,
        String lastName,
        String specialty
) {
}
