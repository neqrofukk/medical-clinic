package com.neqrofukk.medicalclinic.dto.Doctor;

public record DoctorCreateCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialty
) {
}
