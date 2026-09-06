package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDto;
import com.neqrofukk.medicalclinic.dto.Clinic.ClinicUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.mapper.*;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        this.doctorService = new DoctorService(doctorRepository, doctorMapper, doctorDetailsMapper, clinicRepository, clinicService, visitMapper);
    }

    @Test
    void getDoctors_DoctorsExist_DoctorsReturned() {
        // given
        List<Doctor> doctors = new ArrayList<>();
        Doctor doctor1 = new Doctor(1L, "shrink", null, null, null, 1L);
        Doctor doctor2 = new Doctor(2L, "eye doctor", null, null, null, 2L);
        doctors.add(doctor1);
        doctors.add(doctor2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName"));
        Page<Doctor> page = new PageImpl<>(doctors, pageable, doctors.size());
        when(doctorRepository.findAll(pageable)).thenReturn(page);

        // when
        PageResponse<DoctorDto> pageResponse = doctorService.getDoctors(null, pageable);
        List<DoctorDto> result = pageResponse.content();

        // then
        Assertions.assertAll(
                () -> assertEquals(doctors.size(), result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(2L, result.get(1).id())
        );
        verify(doctorRepository).findAll(pageable);
    }

    @Test
    void findById_DoctorExists_DoctorReturned() {
        // given
        Doctor doctor = new Doctor(1L, "shrink", null, null, null, 1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        // when
        DoctorDetailsDto result = doctorService.findById(1L);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id())
        );
        verify(doctorRepository).findById(1L);
    }

    @Test
    void addDoctor_DoctorCreated_DoctorReturned() {
        // given
        DoctorCreateCommand doctorCreateCommand = new DoctorCreateCommand("buziaczek67@serduszko.com", "trudneHaslo2137", "Jan", "Kowalski", "shrink");
        Doctor doctor = doctorMapper.toEntity(doctorCreateCommand);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        // when
        DoctorDto result = doctorService.addDoctor(doctorCreateCommand);

        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("buziaczek67@serduszko.com", result.email())
        );
        verify(doctorRepository).save(doctor);
    }

    @Test
    void updateDoctor_DoctorExists_DoctorUpdatedAndReturned() {
        DoctorUpdateCommand doctorUpdateCommand = new DoctorUpdateCommand("doctor2", "city2", "20-000", "street2", 1);
        Doctor doctor = new Doctor(1L, "doctor1", "city1", "10-000", "street1", 1, null, 2L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        // when

        DoctorDto result = doctorService.updateDoctor(1L, doctorUpdateCommand);
        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("doctor2", result.name())
        );
        verify(doctorRepository).findById(1L);
    }


    @Test
    void deleteDoctor() {
    }

    @Test
    void addClinicToDoctor() {
    }

    @Test
    void removeClinicFromDoctor() {
    }

    @Test
    void findAllVisits() {
    }
}