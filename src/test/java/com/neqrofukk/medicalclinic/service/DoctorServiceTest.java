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
import com.neqrofukk.medicalclinic.exceptions.InvalidSortPropertyException;
import com.neqrofukk.medicalclinic.mapper.*;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

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
        // BUG in the original test: only doctorRepository/doctorMapper were ever mocked here.
        // doctorDetailsMapper, clinicRepository, clinicService and visitMapper stayed null,
        // which NPEs the moment DoctorService touches any of them (findById, addClinicToDoctor,
        // removeClinicFromDoctor, findAllVisits all use at least one of these).
        //
        // doctorDetailsMapper is built with `new DoctorDetailsMapperImpl(...)` rather than
        // Mappers.getMapper(DoctorDetailsMapper.class): DoctorDetailsMapper "uses" ClinicMapper,
        // and with injectionStrategy = InjectionStrategy.CONSTRUCTOR on the @Mapper annotation,
        // the generated impl takes ClinicMapper as a constructor argument instead of a
        // Spring-only @Autowired field - so it can be wired directly, no Spring context and no
        // reflection needed.
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.doctorDetailsMapper = new DoctorDetailsMapperImpl(Mappers.getMapper(ClinicMapper.class));
        this.clinicRepository = Mockito.mock(ClinicRepository.class);
        this.clinicService = Mockito.mock(ClinicService.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.doctorService = new DoctorService(doctorRepository, doctorMapper, doctorDetailsMapper, clinicRepository, clinicService, visitMapper);
    }

    @Test
    void getDoctors_DoctorsExist_DoctorsReturned() {
        // BUG in the original test: it stubbed findAll(Pageable) (the plain JpaRepository
        // overload), but DoctorService actually calls findAll(Specification, Pageable) from
        // JpaSpecificationExecutor - a different overload. The real call was never stubbed
        // and returned null, so page.map(...) NPE'd.
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
        DoctorDto dto1 = result.get(0);
        DoctorDto dto2 = result.get(1);

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(0, pageResponse.page()),
                () -> assertEquals(20, pageResponse.size()),
                () -> assertEquals(2, pageResponse.totalElements()),
                () -> assertEquals(1, pageResponse.totalPages()),
                () -> assertTrue(pageResponse.last()),
                () -> assertEquals(1L, dto1.id()),
                () -> assertEquals("shrink", dto1.specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", dto1.email()),
                () -> assertEquals("Jan", dto1.firstName()),
                () -> assertEquals("Kowalski", dto1.lastName()),
                () -> assertEquals(2L, dto2.id()),
                () -> assertEquals("eye doctor", dto2.specialty()),
                () -> assertEquals("buziaczek69@serduszko.com", dto2.email()),
                () -> assertEquals("Janek", dto2.firstName()),
                () -> assertEquals("Nowak", dto2.lastName())
        );
        verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getDoctors_InvalidSortProperty_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("password"));

        InvalidSortPropertyException ex = assertThrows(InvalidSortPropertyException.class, () -> doctorService.getDoctors(null, pageable));
        assertTrue(ex.getMessage().contains("password"));
        verifyNoInteractions(doctorRepository);
    }

    @Test
    void findById_DoctorExists_DoctorReturned() {
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), user, null, 1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorDetailsDto result = doctorService.findById(1L);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("shrink", result.specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertNotNull(result.clinics()),
                () -> assertTrue(result.clinics().isEmpty())
        );
        verify(doctorRepository).findById(1L);
    }

    @Test
    void findById_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> doctorService.findById(99L));
        assertEquals("Doctor with id 99 not found", ex.getMessage());
    }

    @Test
    void addDoctor_DoctorCreated_DoctorReturned() {
        // Same id-based-equals trap as ClinicServiceTest.addClinic: don't stub save(exactInstance).
        DoctorCreateCommand command = new DoctorCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", "shrink");
        User savedUser = new User(null, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor saved = new Doctor(1L, "shrink", new HashSet<>(), savedUser, null, 0L);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(saved);

        DoctorDto result = doctorService.addDoctor(command);

        ArgumentCaptor<Doctor> captor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(captor.capture());
        Doctor passedToSave = captor.getValue();
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("shrink", result.specialty()),
                // DoctorMapper now maps nested user.email/firstName/lastName (fixed - previously
                // hardcoded to null in the generated mapper).
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals("shrink", passedToSave.getSpecialty()),
                () -> assertNotNull(passedToSave.getUser()),
                () -> assertEquals("buziaczek67@serduszko.com", passedToSave.getUser().getEmail()),
                () -> assertEquals("trudneHaslo2137", passedToSave.getUser().getPassword()),
                () -> assertEquals("Jan", passedToSave.getUser().getFirstName()),
                () -> assertEquals("Kowalski", passedToSave.getUser().getLastName())
        );
    }

    @Test
    void updateDoctor_DoctorExists_DoctorUpdatedAndReturned() {
        // BUG in the original test: Doctor.updateDoctor() unconditionally does
        // Utils.setIfPresent(doctor.email(), user::setEmail) etc. - a bound method reference on
        // this.user. If user is null (as the original test had it), building that method
        // reference throws NPE immediately, before the update logic even runs. A real,
        // persisted Doctor always has a User (that's how it authenticates), so the fix is
        // giving the test entity one, not guarding the production code.
        DoctorUpdateCommand command = new DoctorUpdateCommand("buziaczek69@serduszko.com", "Janek", "Nowak", "eye doctor");
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), user, null, 1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorDto result = doctorService.updateDoctor(1L, command);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                // Original test asserted "doctor2" here - a copy/paste leftover from
                // ClinicServiceTest that never matched what the update command sets.
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
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void deleteDoctor_DoctorExists_DoctorDeleted() {
        doctorService.deleteDoctor(1L);

        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void addClinicToDoctor_DoctorAndClinicExist_DoctorDetailsReturned() {
        // DoctorService delegates the actual entity mutation to ClinicService.linkDoctorAndClinic.
        // With ClinicService mocked (as it should be for a DoctorService unit test), that mutation
        // does not really happen - so we verify the collaboration instead of asserting doctor's
        // clinics collection (asserting the mutation belongs in ClinicServiceTest).
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), user, null, null);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(), 1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        DoctorDetailsDto result = doctorService.addClinicToDoctor(1L, 1L);

        verify(clinicService).linkDoctorAndClinic(clinic, doctor);
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("shrink", result.specialty()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertNotNull(result.clinics()),
                () -> assertTrue(result.clinics().isEmpty())
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
        // clinicService is mocked, so the real unlink never happens here - doctor.getClinics()
        // still contains `clinic` afterwards, and that is exactly what the mapped DTO reflects.
        User user = new User(2L, "buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", null, null, null);
        Clinic clinic = new Clinic(1L, "clinic1", "city1", "10-000", "street1", 1, new HashSet<>(), 1L);
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(Set.of(clinic)), user, null, null);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        DoctorDetailsDto result = doctorService.removeClinicFromDoctor(1L, 1L);

        verify(clinicService).unlinkDoctorAndClinic(clinic, doctor);
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("buziaczek67@serduszko.com", result.email()),
                () -> assertEquals(1, result.clinics().size()),
                () -> assertEquals(1L, result.clinics().get(0).id()),
                () -> assertEquals("clinic1", result.clinics().get(0).name())
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
        Map<Long, VisitDto> byId = new HashMap<>();
        result.forEach(dto -> byId.put(dto.id(), dto));

        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:00:00"), byId.get(1L).startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), byId.get(1L).endTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T12:30:00"), byId.get(2L).startTime()),
                () -> assertEquals(LocalDateTime.parse("2010-02-02T13:00:00"), byId.get(2L).endTime())
        );
        verify(doctorRepository).findById(1L);
    }

    @Test
    void findAllVisits_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(DoctorNotFoundException.class, () -> doctorService.findAllVisits(1L));
        assertEquals("Doctor with id 1 not found", ex.getMessage());
    }
}