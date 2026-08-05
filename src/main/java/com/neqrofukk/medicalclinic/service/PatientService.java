package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.mapper.PatientMapper;
import com.neqrofukk.medicalclinic.mapper.VisitMapper;
import com.neqrofukk.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;
    private final VisitMapper visitMapper;

    public List<PatientDto> findAll() {
        return patientRepository
                .findAll()
                .stream()
                .map(patientMapper::toPatientDto)
                .toList();
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
