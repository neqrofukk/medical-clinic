package com.neqrofukk.medicalclinic.dto.Doctor;

public record DoctorDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialty
) {
}
