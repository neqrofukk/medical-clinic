package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitCreateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.dto.Visit.VisitUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.Visit;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.visit.*;
import com.neqrofukk.medicalclinic.mapper.VisitMapper;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import com.neqrofukk.medicalclinic.repository.PatientRepository;
import com.neqrofukk.medicalclinic.repository.VisitRepository;
import com.neqrofukk.medicalclinic.validators.VisitValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VisitServiceTest {
    VisitService visitService;
    VisitRepository visitRepository;
    VisitMapper visitMapper;
    VisitValidator visitValidator;
    DoctorRepository doctorRepository;
    PatientRepository patientRepository;

    @BeforeEach
    void setup() {
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.visitValidator = Mockito.mock(VisitValidator.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.visitService = new VisitService(visitRepository, visitMapper, visitValidator, patientRepository, doctorRepository);
    }

    @Test
    void getVisits_VisitsExist_VisitsReturned() {
        Doctor doctor = new Doctor(10L, "shrink", null, null, null, null);
        Patient patient = new Patient(20L, "123456", null, null, null, null, null);
        Visit visit1 = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), doctor, patient, null);
        Visit visit2 = new Visit(2L, LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null, null, null);
        List<Visit> visits = List.of(visit1, visit2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("startTime"));
        Page<Visit> page = new PageImpl<>(visits, pageable, visits.size());
        when(visitRepository.findAll(pageable)).thenReturn(page);

        PageResponse<VisitDto> pageResponse = visitService.getVisits(pageable);
        List<VisitDto> result = pageResponse.content();

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:00:00"), result.get(0).startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), result.get(0).endTime()),
                () -> assertEquals(10L, result.get(0).doctorId()),
                () -> assertEquals(20L, result.get(0).patientId()),
                () -> assertEquals(2L, result.get(1).id()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), result.get(1).startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T13:00:00"), result.get(1).endTime()),
                () -> assertNull(result.get(1).doctorId()),
                () -> assertNull(result.get(1).patientId())
        );
        verify(visitRepository).findAll(pageable);
    }

    @Test
    void findById_VisitExists_VisitReturned() {
        Doctor doctor = new Doctor(10L, "shrink", null, null, null, null);
        Patient patient = new Patient(20L, "123456", null, null, null, null, null);
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), doctor, patient, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        VisitDto result = visitService.findById(1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:00:00"), result.startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), result.endTime()),
                () -> assertEquals(10L, result.doctorId()),
                () -> assertEquals(20L, result.patientId())
        );
        verify(visitRepository).findById(1L);
    }

    @Test
    void findById_VisitNotFound_ThrowsException() {
        when(visitRepository.findById(2L)).thenReturn(Optional.empty());

        VisitNotFoundException ex = assertThrows(VisitNotFoundException.class, () -> visitService.findById(2L));
        assertEquals("Visit with id 2 not found", ex.getMessage());
    }

    @Test
    void addVisit_VisitCreated_VisitReturned() {
        Doctor doctor = new Doctor(1L, "shrink", null, null, null, null);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        VisitCreateCommand command = new VisitCreateCommand(LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), 1L);
        Visit saved = new Visit(1L, command.startTime(), command.endTime(), doctor, null, 0L);
        when(visitRepository.save(any(Visit.class))).thenReturn(saved);

        VisitDto result = visitService.addVisit(command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:00:00"), result.startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), result.endTime()),
                () -> assertEquals(1L, result.doctorId())
        );
        verify(visitValidator).validate(any());
    }

    @Test
    void addVisit_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        VisitCreateCommand command = new VisitCreateCommand(LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), 1L);

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> visitService.addVisit(command));
        assertEquals("Doctor with id 1 not found", ex.getMessage());
    }

    @Test
    void addVisit_PastStartDate_ThrowsVisitNotFutureDateException() {
        VisitService realValidatorService = new VisitService(visitRepository, visitMapper, new VisitValidator(), patientRepository, doctorRepository);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), null, new HashSet<>(), null);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        LocalDateTime pastStart = LocalDateTime.now().minusDays(1).withMinute(0).withSecond(0).withNano(0);
        VisitCreateCommand command = new VisitCreateCommand(pastStart, pastStart.plusMinutes(30), 1L);

        VisitNotFutureDateException ex = assertThrows(VisitNotFutureDateException.class, () -> realValidatorService.addVisit(command));
        assertEquals("Visit is not a future date", ex.getMessage());
    }

    @Test
    void addVisit_OverlapsExistingDoctorVisit_ThrowsVisitOverlapException() {
        VisitService realValidatorService = new VisitService(visitRepository, visitMapper, new VisitValidator(), patientRepository, doctorRepository);
        LocalDateTime start = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
        Visit existingVisit = new Visit(10L, start, start.plusMinutes(30), null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), null, new HashSet<>(Set.of(existingVisit)), null);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        VisitCreateCommand command = new VisitCreateCommand(start.plusMinutes(15), start.plusMinutes(45), 1L);

        VisitOverlapException ex = assertThrows(VisitOverlapException.class, () -> realValidatorService.addVisit(command));
        assertEquals("Visit overlaps with a different visit", ex.getMessage());
    }

    @Test
    void updateVisit_VisitExists_VisitUpdatedAndReturned() {
        VisitUpdateCommand command = new VisitUpdateCommand(LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null);
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        VisitDto result = visitService.updateVisit(1L, command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), result.startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T13:00:00"), result.endTime())
        );
        verify(visitRepository).findById(1L);
        verify(visitRepository).save(visit);
    }

    @Test
    void updateVisit_VisitNotFound_ThrowsException() {
        VisitUpdateCommand command = new VisitUpdateCommand(LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null);
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());

        VisitNotFoundException ex = assertThrows(VisitNotFoundException.class, () -> visitService.updateVisit(1L, command));
        assertEquals("Visit with id 1 not found", ex.getMessage());
    }

    @Test
    void deleteVisit_VisitExists_VisitDeleted() {
        visitService.deleteVisit(1L);

        verify(visitRepository).deleteById(1L);
    }

    @Test
    void addPatientToVisit_PatientAndVisitExist_PatientAdded() {
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        Patient patient = new Patient(1L, "123456", null, "600900600", null, new HashSet<>(), null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(visitRepository.save(visit)).thenReturn(visit);

        VisitDto result = visitService.addPatientToVisit(1L, 1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:00:00"), result.startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), result.endTime()),
                () -> assertNull(result.doctorId()),
                () -> assertEquals(1L, result.patientId()),
                () -> assertEquals(patient, visit.getPatient())
        );
        verify(visitRepository).save(visit);
    }

    @Test
    void addPatientToVisit_VisitAlreadyHasPatient_ThrowsException() {
        Patient existingPatient = new Patient(2L, "654321", null, null, null, new HashSet<>(), null);
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, existingPatient, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient(1L, "123456", null, null, null, new HashSet<>(), null)));

        VisitAlreadyTakenException ex = assertThrows(VisitAlreadyTakenException.class, () -> visitService.addPatientToVisit(1L, 1L));
        assertEquals("Visit with id 1 is already taken", ex.getMessage());
    }

    @Test
    void addPatientToVisit_VisitNotFound_ThrowsException() {
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());

        VisitNotFoundException ex = assertThrows(VisitNotFoundException.class, () -> visitService.addPatientToVisit(1L, 1L));
        assertEquals("Visit with id 1 not found", ex.getMessage());
    }

    @Test
    void addPatientToVisit_PatientNotFound_ThrowsException() {
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        PatientNotFoundException ex = assertThrows(PatientNotFoundException.class, () -> visitService.addPatientToVisit(1L, 1L));
        assertEquals("Patient with id 1 not found", ex.getMessage());
    }

    @Test
    void removePatientFromVisit_VisitExists_PatientRemoved() {
        Patient patient = new Patient(1L, "123456", null, "600900600", null, new HashSet<>(), null);
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, patient, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(visitRepository.save(visit)).thenReturn(visit);

        VisitDto result = visitService.removePatientFromVisit(1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:00:00"), result.startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), result.endTime()),
                () -> assertNull(result.patientId()),
                () -> assertNull(visit.getPatient())
        );
        verify(visitRepository).findById(1L);
        verify(visitRepository).save(visit);
    }

    @Test
    void removePatientFromVisit_VisitNotFound_ThrowsException() {
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());

        VisitNotFoundException ex = assertThrows(VisitNotFoundException.class, () -> visitService.removePatientFromVisit(1L));
        assertEquals("Visit with id 1 not found", ex.getMessage());
    }
}