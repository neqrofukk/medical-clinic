package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitCreateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.dto.Visit.VisitUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.Visit;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        // given
        List<Visit> visits = new ArrayList<>();
        Visit visit1 = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        Visit visit2 = new Visit(2L, LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null, null, null);
        visits.add(visit1);
        visits.add(visit2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("startDate"));
        Page<Visit> page = new PageImpl<>(visits, pageable, visits.size());
        when(visitRepository.findAll(pageable)).thenReturn(page);

        // when
        PageResponse<VisitDto> pageResponse = visitService.getVisits(pageable);
        List<VisitDto> result = pageResponse.content();

        // then
        Assertions.assertAll(
                () -> assertEquals(visits.size(), result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(2L, result.get(1).id())
        );
        verify(visitRepository).findAll(pageable);
    }

    @Test
    void findById_ClinicExists_ClinicReturned() {
        // given
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);

        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        // when
        VisitDto result = visitService.findById(1L);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id())
        );
        verify(visitRepository).findById(1L);
    }

    @Test
    void addVisit_VisitCreated_VisitReturned() {
        // given
        VisitCreateCommand visitCreateCommand = new VisitCreateCommand(LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null);
        Visit visit = (new Visit()).addVisit(visitCreateCommand, null);
        visit.setId(1L);
        when(visitRepository.save(visit)).thenReturn(visit);

        // when
        VisitDto result = visitService.addVisit(visitCreateCommand);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("2010-02-02T12:00:00", result.startTime().toString()),
                () -> assertEquals("2010-02-02T12:30:00", result.endTime().toString())
        );
        verify(visitRepository).save(visit);
    }

    @Test
    void updateVisit_ClinicExists_ClinicUpdatedAndReturned() {
        VisitUpdateCommand visitUpdateCommand = new VisitUpdateCommand(LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null);
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        // when

        VisitDto result = visitService.updateVisit(1L, visitUpdateCommand);
        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("2010-02-02T12:30:00", result.startTime().toString())
        );
        verify(visitRepository).findById(1L);
    }

    @Test
    void deleteVisit_VisitDeleteInvoked_VisitDeleted() {
        // given
        // when
        visitRepository.deleteById(1L);
        // then
        verify(visitRepository).deleteById(1L);
    }

    @Test
    void addPatientToVisit() {
        // given
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", null, null, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // when
        visitService.addPatientToVisit(1L, 1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(patient, visit.getPatient())
        );

    }

    @Test
    void removePatientFromVisit() {
        // given
        Patient patient = new Patient(1L, "123456", LocalDate.parse("2000-01-01"), "600900600", null, null, null);
        Visit visit = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, patient, null);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // when
        visitService.removePatientFromVisit(1L);

        // then
        Assertions.assertAll(
                () -> assertNotEquals(visit.getPatient(), patient)
        );
    }
}