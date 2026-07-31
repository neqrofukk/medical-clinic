package com.neqrofukk.medicalclinic.dto.User;

public record UserUpdateCommand(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
