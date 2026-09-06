package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.Visit;
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
import java.time.LocalDateTime;
import java.util.*;

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
    void updatePatient_PatientExists_PatientUpdatedAndReturned() {
        // given
        PatientUpdateCommand patientUpdateCommand = new PatientUpdateCommand("buziaczek69@serduszko.com", "987654", "Janek", "Nowak", LocalDate.parse("2010-02-02"), "700900500");
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", null, null, null);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        // when

        PatientDto result = patientService.updatePatient(1L, patientUpdateCommand);
        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("987654", result.idCardNo())
        );
        verify(patientRepository).findById(1L);

    }

    @Test
    void deletePatient_PatientDeleteInvoked_PatientDeleted() {
        // given
        // when
        patientRepository.deleteById(1L);
        // then
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void findAllVisits() {
        // given
        Set<Visit> visits = new HashSet<>();
        Visit visit1 = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        Visit visit2 = new Visit(2L, LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null, null, null);
        visits.add(visit1);
        visits.add(visit2);
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", null, visits, null);
        Set<VisitDto> expected = Set.of(
                visitMapper.toVisitDto(visit1),
                visitMapper.toVisitDto(visit2)
        );
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // when
        Set<VisitDto> result = patientService.findAllVisits(1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(expected, result)
        );
        verify(patientRepository).findById(1L);
    }

}