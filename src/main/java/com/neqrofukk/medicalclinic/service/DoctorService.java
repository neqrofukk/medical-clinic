package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.mapper.DoctorDetailsMapper;
import com.neqrofukk.medicalclinic.mapper.DoctorMapper;
import com.neqrofukk.medicalclinic.mapper.VisitMapper;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import com.neqrofukk.medicalclinic.specifications.DoctorSpecifications;
import com.neqrofukk.medicalclinic.validators.SortValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "lastName", "speciality");

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final DoctorDetailsMapper doctorDetailsMapper;
    private final ClinicRepository clinicRepository;
    private final ClinicService clinicService;
    private final VisitMapper visitMapper;

    @Transactional(readOnly = true)
    public PageResponse<DoctorDto> getDoctors(String specialty, Pageable pageable) {
        SortValidator.validate(pageable.getSort(), ALLOWED_SORT_FIELDS);

        Specification<Doctor> spec = DoctorSpecifications.build(specialty);

        Page<Doctor> page = doctorRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(doctorMapper::toDoctorDto));
    }

    public DoctorDetailsDto findById(Long id) {
        Doctor doctorDb = getDoctorDb(id);
        return doctorDetailsMapper.toDoctorDetailsDto(doctorDb);
    }

    public DoctorDto addDoctor(DoctorCreateCommand doctor) {
        Doctor doctorEntity = (new Doctor()).addDoctor(doctor);
        Doctor doctorDb = doctorRepository.save(doctorEntity);
        return doctorMapper.toDoctorDto(doctorDb);
    }

    public DoctorDto updateDoctor(Long id, DoctorUpdateCommand doctor) {
        Doctor doctorDb = getDoctorDb(id);
        doctorDb.updateDoctor(doctor);
        doctorRepository.save(doctorDb);
        return doctorMapper.toDoctorDto(doctorDb);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public DoctorDetailsDto addClinicToDoctor(Long doctorId, Long clinicId) {
        Doctor doctorDb = getDoctorDb(doctorId);
        Clinic clinicDb = getClinicDb(clinicId);
        clinicService.linkDoctorAndClinic(clinicDb, doctorDb);
        return doctorDetailsMapper.toDoctorDetailsDto(doctorDb);
    }

    public DoctorDetailsDto removeClinicFromDoctor(Long doctorId, Long clinicId) {
        Doctor doctorDb = getDoctorDb(doctorId);
        Clinic clinicDb = getClinicDb(clinicId);
        clinicService.unlinkDoctorAndClinic(clinicDb, doctorDb);
        return doctorDetailsMapper.toDoctorDetailsDto(doctorDb);
    }

    public Set<VisitDto> findAllVisits(Long doctorId) {
        Doctor doctor = getDoctorDb(doctorId);
        return doctor.getVisits()
                .stream()
                .map(visitMapper::toVisitDto)
                .collect(Collectors.toSet());
    }

    private Doctor getDoctorDb(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    private Clinic getClinicDb(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new ClinicNotFoundException(id));
    }
}
