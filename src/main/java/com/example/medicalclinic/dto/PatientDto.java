package com.example.clinic.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
