package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDto;
import com.neqrofukk.medicalclinic.dto.Doctor.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.mapper.DoctorDetailsMapper;
import com.neqrofukk.medicalclinic.mapper.DoctorMapper;
import com.neqrofukk.medicalclinic.repository.ClinicRepository;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import com.neqrofukk.medicalclinic.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final DoctorDetailsMapper doctorDetailsMapper;
    private final ClinicRepository clinicRepository;
    private final ClinicService clinicService;

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll().stream().map(doctorMapper::toDoctorDto).toList();
    }

    public DoctorDetailsDto findById(Long id) {
        Doctor doctorDb = getDoctorDb(id);
        return doctorDetailsMapper.toDoctorDetailsDto(doctorDb);
    }

    public DoctorDto addDoctor(DoctorCreateCommand doctor) {
        User user = new User();
        user.setEmail(doctor.email());
        user.setFirstName(doctor.firstName());
        user.setLastName(doctor.lastName());
        user.setPassword(doctor.password());

        Doctor doctorEntity = new Doctor();
        doctorEntity.setSpecialty(doctor.specialty());
        doctorEntity.setUser(user);

        Doctor doctorDb = doctorRepository.save(doctorEntity);
        return doctorMapper.toDoctorDto(doctorDb);
    }

    public DoctorDto updateDoctor(Long id, DoctorUpdateCommand doctor) {
        Doctor doctorDb = getDoctorDb(id);
        User userDb = doctorDb.getUser();

        Utils.setIfPresent(doctor.specialty(), doctorDb::setSpecialty);
        Utils.setIfPresent(doctor.email(), userDb::setEmail);
        Utils.setIfPresent(doctor.firstName(), userDb::setFirstName);
        Utils.setIfPresent(doctor.lastName(), userDb::setLastName);

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

    private Doctor getDoctorDb(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
    }

    private Clinic getClinicDb(Long id) {
        return clinicRepository.findById(id).orElseThrow(() -> new ClinicNotFoundException(id));
    }
}
