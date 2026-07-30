package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.PatientDto;
import com.neqrofukk.medicalclinic.dto.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.mapper.PatientMapper;
import com.neqrofukk.medicalclinic.repository.PatientRepository;
import com.neqrofukk.medicalclinic.util.Utils;
import com.neqrofukk.medicalclinic.validators.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;
    private final EmailValidator emailValidator;

    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream().map(patientMapper::toPatientDto).toList();
    }

    public PatientDto findById(Long id) {
        Patient patientDb = getPatientDb(id);
        return patientMapper.toPatientDto(patientDb);
    }

    public PatientDto addPatient(PatientCreateCommand patient) {
        User user = new User();
        user.setEmail(patient.email());
        user.setFirstName(patient.firstName());
        user.setLastName(patient.lastName());
        user.setPassword(patient.password());

        Patient patientEntity = new Patient();
        patientEntity.setIdCardNo(patient.idCardNo());
        patientEntity.setBirthDay(patient.birthDay());
        patientEntity.setPhoneNumber(patient.phoneNumber());
        patientEntity.setUser(user);

        Patient savedPatient = patientRepository.save(patientEntity);
        return patientMapper.toPatientDto(savedPatient);
    }


    public PatientDto updatePatient(Long id, PatientUpdateCommand patient) {
        Patient patientDb = getPatientDb(id);
        User userDb = patientDb.getUser();

        Utils.setIfPresent(patient.idCardNo(), patientDb::setIdCardNo);
        Utils.setIfNotNullDate(patient.birthDay(), patientDb::setBirthDay);
        Utils.setIfPresent(patient.phoneNumber(), patientDb::setPhoneNumber);
        Utils.setIfPresent(patient.email(), userDb::setEmail);
        Utils.setIfPresent(patient.firstName(), userDb::setFirstName);
        Utils.setIfPresent(patient.lastName(), userDb::setLastName);


        Patient updatedPatient = patientRepository.save(patientDb);
        return patientMapper.toPatientDto(updatedPatient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
    

    private Patient getPatientDb(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
    }
}
