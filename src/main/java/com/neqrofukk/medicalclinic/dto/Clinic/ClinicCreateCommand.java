package com.neqrofukk.medicalclinic.dto.Clinic;

public record ClinicCreateCommand(
        String name,
        String city,
        String zipCode,
        String street,
        Integer streetNumber
) {
}
