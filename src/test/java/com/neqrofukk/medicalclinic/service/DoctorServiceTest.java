package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.entity.Visit;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.mapper.*;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DoctorServiceTest {
    DoctorService doctorService;
    DoctorRepository doctorRepository;
    DoctorMapper doctorMapper;
    DoctorDetailsMapper doctorDetailsMapper;
    ClinicRepository clinicRepository;
    ClinicService clinicService;
    VisitMapper visitMapper;

    @BeforeEach
    void setup() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        ClinicMapper clinicMapper = Mappers.getMapper(ClinicMapper.class);
        this.doctorDetailsMapper = new DoctorDetailsMapperImpl(clinicMapper);
        this.clinicRepository = Mockito.mock(ClinicRepository.class);
        ClinicDetailsMapper clinicDetailsMapper = new ClinicDetailsMapperImpl(doctorMapper);
        ClinicService realClinicService = new ClinicService(clinicRepository, clinicMapper, clinicDetailsMapper, doctorRepository);
        this.clinicService = Mockito.spy(realClinicService);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.doctorService = new DoctorService(doctorRepository, doctorMapper, doctorDetailsMapper, clinicRepository, clinicService, visitMapper);
    }

    @Test
    void getDoctors_DoctorsExist_DoctorsReturned() {
        User user1 = new User(10L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        User user2 = new User(20L, "buziaczek69@serduszko.com", "trudneHaslo6767", "Janek", "Nowak", null, null, null);
        Doctor doctor1 = new Doctor(1L, "shrink", new HashSet<>(), user1, null, 1L);
        Doctor doctor2 = new Doctor(2L, "eye doctor", new HashSet<>(), user2, null, 2L);
        List<Doctor> doctors = List.of(doctor1, doctor2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName"));
        Page<Doctor> page = new PageImpl<>(doctors, pageable, doctors.size());
        when(doctorRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        PageResponse<DoctorDto> pageResponse = doctorService.getDoctors(null, pageable);
        List<DoctorDto> result = pageResponse.content();

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals("shrink", result.get(0).specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", result.get(0).email()),
                () -> assertEquals("Jan", result.get(0).firstName()),
                () -> assertEquals("Kowalski", result.get(0).lastName()),
                () -> assertEquals(2L, result.get(1).id()),
                () -> assertEquals("eye doctor", result.get(1).specialty()),
                () -> assertEquals("buziaczek69@serduszko.com", result.get(1).email()),
                () -> assertEquals("Janek", result.get(1).firstName()),
                () -> assertEquals("Nowak", result.get(1).lastName())
        );
        verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findById_DoctorExists_DoctorReturned() {
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", null, user, null, 1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorDetailsDto result = doctorService.findById(1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("shrink", result.specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName())
        );
        verify(doctorRepository).findById(1L);
    }

    @Test
    void findById_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> doctorService.findById(2L));
        assertEquals("Doctor with id 2 not found", ex.getMessage());
    }

    @Test
    void addDoctor_DoctorCreated_DoctorReturned() {
        DoctorCreateCommand command = new DoctorCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", "shrink");
        User savedUser = new User(null, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor saved = new Doctor(1L, "shrink", new HashSet<>(), savedUser, null, 0L);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(saved);

        DoctorDto result = doctorService.addDoctor(command);

        verify(doctorRepository).save(any(Doctor.class));
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("shrink", result.specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName())
        );
    }

    @Test
    void updateDoctor_DoctorExists_DoctorUpdatedAndReturned() {
        DoctorUpdateCommand command = new DoctorUpdateCommand("buziaczek69@serduszko.com", "Janek", "Nowak", "eye doctor");
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", null, user, null, 1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorDto result = doctorService.updateDoctor(1L, command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("eye doctor", result.specialty()),
                () -> assertEquals("buziaczek69@serduszko.com", result.email()),
                () -> assertEquals("Janek", result.firstName()),
                () -> assertEquals("Nowak", result.lastName())
        );
        verify(doctorRepository).save(doctor);
    }

    @Test
    void updateDoctor_DoctorNotFound_ThrowsException() {
        DoctorUpdateCommand command = new DoctorUpdateCommand("buziaczek69@serduszko.com", "Janek", "Nowak", "eye doctor");
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> doctorService.updateDoctor(1L, command));
        assertEquals("Doctor with id 1 not found", ex.getMessage());
        verify(doctorRepository).findById(1L);
    }

    @Test
    void deleteDoctor_DoctorExists_DoctorDeleted() {
        doctorService.deleteDoctor(1L);

        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void addClinicToDoctor_DoctorAndClinicExist_DoctorDetailsReturned() {
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), user, null, null);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(), 1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        DoctorDetailsDto result = doctorService.addClinicToDoctor(1L, 1L);

        verify(clinicService).linkDoctorAndClinic(clinic, doctor);
        Assertions.assertAll(
                () -> assertTrue(doctor.getClinics().contains(clinic)),
                () -> assertTrue(clinic.getDoctors().contains(doctor)),
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("shrink", result.specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals(1, result.clinics().size()),
                () -> assertEquals(1L, result.clinics().get(0).id()),
                () -> assertEquals("clinic1", result.clinics().get(0).name())
        );
    }

    @Test
    void addClinicToDoctor_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> doctorService.addClinicToDoctor(1L, 1L));
        assertEquals("Doctor with id 1 not found", ex.getMessage());
        verifyNoInteractions(clinicRepository, clinicService);
    }

    @Test
    void addClinicToDoctor_ClinicNotFound_ThrowsException() {
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), null, null, null);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        ClinicNotFoundException ex = assertThrows(ClinicNotFoundException.class, () -> doctorService.addClinicToDoctor(1L, 1L));
        assertEquals("Clinic with id 1 not found", ex.getMessage());
        verifyNoInteractions(clinicService);
    }

    @Test
    void removeClinicFromDoctor_ClinicExistsInDoctor_ClinicRemoved() {
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(), 1L);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(Set.of(clinic)), user, null, null);
        clinic.getDoctors().add(doctor);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        DoctorDetailsDto result = doctorService.removeClinicFromDoctor(1L, 1L);

        verify(clinicService).unlinkDoctorAndClinic(clinic, doctor);
        Assertions.assertAll(
                () -> assertFalse(doctor.getClinics().contains(clinic)),
                () -> assertFalse(clinic.getDoctors().contains(doctor)),
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertTrue(result.clinics().isEmpty())
        );
    }

    @Test
    void removeClinicFromDoctor_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> doctorService.removeClinicFromDoctor(1L, 1L));
        assertEquals("Doctor with id 1 not found", ex.getMessage());
        verifyNoInteractions(clinicRepository, clinicService);
    }

    @Test
    void findAllVisits_VisitsExist_VisitsReturned() {
        Set<Visit> visits = new HashSet<>();
        Visit visit1 = new Visit(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null, null);
        Visit visit2 = new Visit(2L, LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null, null, null);
        visits.add(visit1);
        visits.add(visit2);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), null, visits, null);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Set<VisitDto> result = doctorService.findAllVisits(1L);

        Set<VisitDto> expected = Set.of(
                new VisitDto(1L, LocalDateTime.parse("2010-02-02T12:00:00"), LocalDateTime.parse("2010-02-02T12:30:00"), null, null),
                new VisitDto(2L, LocalDateTime.parse("2010-02-02T12:30:00"), LocalDateTime.parse("2010-02-02T13:00:00"), null, null)
        );
        assertEquals(expected, result);
        verify(doctorRepository).findById(1L);
    }

    @Test
    void findAllVisits_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> doctorService.findAllVisits(1L));
        assertEquals("Doctor with id 1 not found", ex.getMessage());
    }
}