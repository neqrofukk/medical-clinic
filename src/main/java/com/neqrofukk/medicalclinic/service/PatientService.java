package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.mapper.PatientMapper;
import com.neqrofukk.medicalclinic.mapper.VisitMapper;
import com.neqrofukk.medicalclinic.repository.PatientRepository;
import com.neqrofukk.medicalclinic.validators.SortValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "lastName", "firstName", "birthDay");

    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;
    private final VisitMapper visitMapper;

    @Transactional(readOnly = true)
    public PageResponse<PatientDto> getPatients(Pageable pageable) {
        SortValidator.validate(pageable.getSort(), ALLOWED_SORT_FIELDS);

        Page<Patient> page = patientRepository.findAll(pageable);
        return PageResponse.from(page.map(patientMapper::toPatientDto));
    }

    public PatientDto findById(Long id) {
        Patient patientDb = getPatientDb(id);
        return patientMapper.toPatientDto(patientDb);
    }

    public PatientDto addPatient(PatientCreateCommand patient) {
        Patient patientEntity = (new Patient()).addPatient(patient);
        Patient savedPatient = patientRepository.save(patientEntity);
        return patientMapper.toPatientDto(savedPatient);
    }

    public PatientDto updatePatient(Long id, PatientUpdateCommand patient) {
        Patient patientDb = getPatientDb(id);
        patientDb.updatePatient(patient);
        Patient updatedPatient = patientRepository.save(patientDb);
        return patientMapper.toPatientDto(updatedPatient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public Set<VisitDto> findAllVisits(Long patientId) {
        Patient patient = getPatientDb(patientId);
        return patient.getVisits()
                .stream()
                .map(visitMapper::toVisitDto)
                .collect(Collectors.toSet());
    }

    private Patient getPatientDb(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }
}
