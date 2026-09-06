package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.mapper.PatientMapper;
import com.neqrofukk.medicalclinic.mapper.VisitMapper;
import com.neqrofukk.medicalclinic.repository.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatientServiceTest {

    PatientService patientService;
    PatientRepository patientRepository;
    PatientMapper patientMapper;
    VisitMapper visitMapper;

    @BeforeEach
    void setup() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.patientService = new PatientService(patientMapper, patientRepository, visitMapper);
    }

    @Test
    void getPatients_PatientsExist_PatientsReturned() {
        // given
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", null, null, null));
        patients.add(new Patient(2L, "987654", LocalDate.parse("2010-02-02"), "700900500", null, null, null));
        Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName"));
        Page<Patient> page = new PageImpl<>(patients, pageable, 2);
        when(patientRepository.findAll(pageable)).thenReturn(page);

        // when
        PageResponse<PatientDto> pageResponse = patientService.getPatients(pageable);
        List<PatientDto> result = pageResponse.content();

        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(2L, result.get(1).id())
        );
        verify(patientRepository).findAll(pageable);
    }

    @Test
    void findById_PatientExists_PatientReturned() {
        // given
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", null, null, null);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        // when
        PatientDto result = patientService.findById(1L);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id())
        );
        verify(patientRepository).findById(1L);
    }

    @Test
    void addPatient_PatientCreated_PatientReturned() {
        // given
        PatientCreateCommand patientCreateCommand = new PatientCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "123456", "Jan", "Kowalski", LocalDate.parse("2000-01-01"), "600900600");
        Patient patient = (new Patient()).addPatient(patientCreateCommand);
        patient.setId(1L);
        when(patientRepository.save(patient)).thenReturn(patient);

        // when
        PatientDto result = patientService.addPatient(patientCreateCommand);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("123456", result.idCardNo())
        );
        verify(patientRepository).save(patient);
    }

    @Test
    void updatePatient() {
        // given
        PatientUpdateCommand patientUpdateCommand = new PatientUpdateCommand("buziaczek67@serduszko.com", "123456", "Jan", "Kowalski", LocalDate.parse("2000-01-01"), "600900600");
        // when
        // then

    }

    @Test
    void deletePatient() {
        // given
        // when
        // then
    }

    @Test
    void findAllVisits() {
        // given
        // when
        // then
    }
}