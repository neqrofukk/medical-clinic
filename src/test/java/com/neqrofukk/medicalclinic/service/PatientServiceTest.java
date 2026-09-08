package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Patient.PatientCreateCommand;
import com.neqrofukk.medicalclinic.dto.Patient.PatientDto;
import com.neqrofukk.medicalclinic.dto.Patient.PatientUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.entity.Visit;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        User user1 = new User(10L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        User user2 = new User(20L, "buziaczek69@serduszko.com", "trudneHaslo6767", "Janek", "Nowak", null, null, null);
        Patient patient1 = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", user1, new HashSet<>(), null);
        Patient patient2 = new Patient(2L, "987654", LocalDate.parse("2010-02-02"), "700900500", user2, new HashSet<>(), null);
        List<Patient> patients = List.of(patient1, patient2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName"));
        Page<Patient> page = new PageImpl<>(patients, pageable, 2);
        when(patientRepository.findAll(pageable)).thenReturn(page);

        PageResponse<PatientDto> pageResponse = patientService.getPatients(pageable);
        List<PatientDto> result = pageResponse.content();

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("buziaczek67@serduszko.com", result.get(0).email()),
                () -> assertEquals("123456", result.get(0).idCardNo()),
                () -> assertEquals("Jan", result.get(0).firstName()),
                () -> assertEquals("Kowalski", result.get(0).lastName()),
                () -> assertEquals(LocalDate.parse("2000-01-01"), result.get(0).birthDay()),
                () -> assertEquals("600900600", result.get(0).phoneNumber()),
                () -> assertEquals(2L, result.get(1).id()),
                () -> assertEquals("buziaczek69@serduszko.com", result.get(1).email()),
                () -> assertEquals("987654", result.get(1).idCardNo()),
                () -> assertEquals("Janek", result.get(1).firstName()),
                () -> assertEquals("Nowak", result.get(1).lastName()),
                () -> assertEquals(LocalDate.parse("2010-02-02"), result.get(1).birthDay()),
                () -> assertEquals("700900500", result.get(1).phoneNumber())
        );
        verify(patientRepository).findAll(pageable);
    }

    @Test
    void findById_PatientExists_PatientReturned() {
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", user, null, null);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDto result = patientService.findById(1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("123456", result.idCardNo()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals(LocalDate.parse("2000-01-01"), result.birthDay()),
                () -> assertEquals("600900600", result.phoneNumber())
        );
        verify(patientRepository).findById(1L);
    }

    @Test
    void findById_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        PatientNotFoundException ex = assertThrows(PatientNotFoundException.class, () -> patientService.findById(2L));
        assertEquals("Patient with id 2 not found", ex.getMessage());
    }

    @Test
    void addPatient_PatientCreated_PatientReturned() {
        PatientCreateCommand command = new PatientCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "123456", "Jan", "Kowalski", LocalDate.parse("2000-01-01"), "600900600");
        Patient patient = (new Patient()).addPatient(command);
        patient.setId(1L);
        when(patientRepository.save(patient)).thenReturn(patient);

        PatientDto result = patientService.addPatient(command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("123456", result.idCardNo()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals(LocalDate.parse("2000-01-01"), result.birthDay()),
                () -> assertEquals("600900600", result.phoneNumber())
        );
        verify(patientRepository).save(patient);
    }

    @Test
    void updatePatient_PatientExists_PatientUpdatedAndReturned() {
        PatientUpdateCommand command = new PatientUpdateCommand("buziaczek69@serduszko.com", "987654", "Janek", "Nowak", LocalDate.parse("2010-02-02"), "700900500");
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", user, new HashSet<>(), null);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientDto result = patientService.updatePatient(1L, command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek69@serduszko.com", result.email()),
                () -> assertEquals("987654", result.idCardNo()),
                () -> assertEquals("Janek", result.firstName()),
                () -> assertEquals("Nowak", result.lastName()),
                () -> assertEquals(LocalDate.parse("2010-02-02"), result.birthDay()),
                () -> assertEquals("700900500", result.phoneNumber())
        );
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(patient);
    }

    @Test
    void updatePatient_PatientNotFound_ThrowsException() {
        PatientUpdateCommand command = new PatientUpdateCommand("buziaczek69@serduszko.com", "987654", "Janek", "Nowak", LocalDate.parse("2010-02-02"), "700900500");
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        PatientNotFoundException ex = assertThrows(PatientNotFoundException.class, () -> patientService.updatePatient(1L, command));
        assertEquals("Patient with id 1 not found", ex.getMessage());
    }

    @Test
    void deletePatient_PatientExists_PatientDeleted() {
        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
    }

    @Test
    void findAllVisits_VisitsExist_VisitsReturned() {
        Set<Visit> visits = new HashSet<>();
        Visit visit1 = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        Visit visit2 = new Visit(2L, LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null, null, null);
        visits.add(visit1);
        visits.add(visit2);
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", null, visits, null);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Set<VisitDto> result = patientService.findAllVisits(1L);

        Set<VisitDto> expected = Set.of(
                new VisitDto(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null),
                new VisitDto(2L, LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null, null)
        );
        assertEquals(expected, result);
        verify(patientRepository).findById(1L);
    }

    @Test
    void findAllVisits_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        PatientNotFoundException ex = assertThrows(PatientNotFoundException.class, () -> patientService.findAllVisits(1L));
        assertEquals("Patient with id 1 not found", ex.getMessage());
    }
}