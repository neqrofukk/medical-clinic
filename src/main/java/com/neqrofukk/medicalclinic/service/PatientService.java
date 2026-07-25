package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.PatientDto;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.exceptions.InvalidEmailException;
import com.neqrofukk.medicalclinic.exceptions.InvalidPasswordException;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.mapper.PatientMapper;
import com.neqrofukk.medicalclinic.repository.PatientRepository;
import com.neqrofukk.medicalclinic.validators.EmailValidator;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository repository;
    private final EmailValidator emailValidator;
    private final PatientMapper mapperInstance;
    private Patient patient;

    public List<PatientDto> findAll() {
        return repository.findAll().stream().map(mapperInstance::toPatientDto).toList();
    }

    public PatientDto findByEmail(@NonNull String email) {
        if (!emailValidator.validate(email)) {
            throw new InvalidEmailException(email);
        }
        Patient patient = repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return mapperInstance.toPatientDto(patient);
    }

    public PatientDto addPatient(@NonNull PatientCreateCommand patientCommand) {
        String emailNormalizer = EmailValidator.normalize(patientCommand.email());

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(patientCommand.email());
        }

        Patient patient = mapperInstance.toEntity(patientCommand);
        patient.setEmail(emailNormalizer);
        repository.create(patient);
        return mapperInstance.toPatientDto(patient);
    }

    public void deleteByEmail(@NonNull String email) {
        String emailNormalizer = EmailValidator.normalize(email);

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(email);
        }
        repository.delete(emailNormalizer);
    }

    public PatientDto updatePatient(@NonNull String email, @NonNull PatientCreateCommand patientCommand) {
        Patient patient = mapperInstance.toEntity(patientCommand);
        patient.setEmail(EmailValidator.normalize(email));
        if (!emailValidator.validate(patient.getEmail())) {
            throw new InvalidEmailException(patient.getEmail());
        }
        repository.update(email, patient);
        return mapperInstance.toPatientDto(patient);
    }

    public void updatePassword(@NonNull String newPassword, @NonNull String email) {
        if(newPassword.isBlank()){
            throw new InvalidPasswordException();
        }
        String emailNormalizer = EmailValidator.normalize(email);
        Patient patientToUpdate = repository.findByEmail(EmailValidator.normalize(email))
                .orElseThrow(() -> new PatientNotFoundException(email));
        patientToUpdate.setPassword(newPassword);
        repository.update(emailNormalizer,patientToUpdate);
    }
}

