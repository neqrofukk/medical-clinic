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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;
    private final EmailValidator emailValidator;

    @Override
    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream().map(patientMapper::toPatientDto).toList();
    };

    @Override
    public PatientDto findByEmail(String email) {
        Patient patientDb = patientRepository.findByEmail(email).orElseThrow(() -> new PatientNotFoundException(email));
        return patientMapper.toPatientDto(patientDb);
    };

    @Override
    public PatientDto addPatient(PatientCreateCommand patient) {
        String normalizedEmail = EmailValidator.normalize(patient.email());
        if (!emailValidator.validate(normalizedEmail)) {
            throw new InvalidEmailException(patient.email());
        }
        Patient savedPatient = patientRepository.save(patientMapper.toEntity(patient));
        return patientMapper.toPatientDto(savedPatient);
    };

    @Override
    public void deleteByEmail(String email) {
        patientRepository.deleteByEmail(email);
    };

    @Override
    public PatientDto updatePatient(String email, PatientCreateCommand patient) {
        Patient patientDB = patientRepository.findByEmail(email).orElseThrow(() -> new PatientNotFoundException(email));

        if (Objects.nonNull(patient.email()) && !"".equalsIgnoreCase(patient.email())) {
            patientDB.setEmail(patient.email());
        }
//        Intentionally ignoring password update
//        if (Objects.nonNull(patient.password()) && !"".equalsIgnoreCase(patient.password())) {
//            patientDB.setPassword(patient.password());
//        }
        if (Objects.nonNull(patient.idCardNo()) && !"".equalsIgnoreCase(patient.idCardNo())) {
            patientDB.setIdCardNo(patient.idCardNo());
        }
        if (Objects.nonNull(patient.firstName()) && !"".equalsIgnoreCase(patient.firstName())) {
            patientDB.setFirstName(patient.firstName());
        }
        if (Objects.nonNull(patient.lastName()) && !"".equalsIgnoreCase(patient.lastName())) {
            patientDB.setLastName(patient.lastName());
        }
        if (Objects.nonNull(patient.birthDay()) && !"".equalsIgnoreCase(String.valueOf(patient.birthDay()))) {
            patientDB.setBirthDay(patient.birthDay());
        }
        if (Objects.nonNull(patient.phoneNumber()) && !"".equalsIgnoreCase(patient.phoneNumber())) {
            patientDB.setPhoneNumber(patient.phoneNumber());
        }

//        Full Update
//        Patient updatedPatient = patientRepository.save(patientDB);
//        return patientMapper.toPatientDto(updatedPatient);

        Patient updatedPatient = patientRepository.save(patientDB);
        return patientMapper.toPatientDto(updatedPatient);
    };

    @Override
    public void updatePassword(String email, String newPassword) {
        if (newPassword.isBlank()) {
            throw new InvalidPasswordException();
        }
        Patient patientDB = patientRepository.findByEmail(email).orElseThrow(() -> new PatientNotFoundException(email));
        patientDB.setPassword(newPassword);
        patientRepository.save(patientDB);
    };
}
