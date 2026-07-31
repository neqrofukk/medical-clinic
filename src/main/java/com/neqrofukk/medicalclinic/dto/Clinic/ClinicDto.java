package com.neqrofukk.medicalclinic.dto.Clinic;

public record ClinicDto(
        Long id,
        String name,
        String city,
        String zipCode,
        String street,
        Integer streetNumber
) {
}
