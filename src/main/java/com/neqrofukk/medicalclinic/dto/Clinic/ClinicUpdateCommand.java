package com.neqrofukk.medicalclinic.dto.Clinic;

public record ClinicUpdateCommand(
        String name,
        String city,
        String zipCode,
        String street,
        Integer streetNumber
) {}
