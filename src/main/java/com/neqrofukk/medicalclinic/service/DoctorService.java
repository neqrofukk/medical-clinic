package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.DoctorDto;
import com.neqrofukk.medicalclinic.dto.DoctorUpdateCommand;
import com.neqrofukk.medicalclinic.entity.Clinic;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.User;
import com.neqrofukk.medicalclinic.exceptions.ClinicNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
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
    private final ClinicRepository clinicRepository;
    private final DoctorMapper doctorMapper;

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll().stream().map(doctorMapper::toDoctorDto).toList();
    }

    public DoctorDto findById(Long id) {
        Doctor doctorDb = getDoctorDb(id);
        return doctorMapper.toDoctorDto(doctorDb);
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

        Clinic clinicDb = getClinicDb(doctor.clinicId());
        doctorEntity.setClinic(clinicDb);

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

        if (doctor.clinicId() != null) {
            Clinic clinicDb = getClinicDb(doctor.clinicId());
            doctorDb.setClinic(clinicDb);
        }

        doctorRepository.save(doctorDb);

        return doctorMapper.toDoctorDto(doctorDb);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public DoctorDto addDoctorToClinic(Long id, Long clinicId) {
        Clinic clinicDb = getClinicDb(clinicId);
        Doctor doctorDb = getDoctorDb(id);

        doctorDb.setClinic(clinicDb);

        return doctorMapper.toDoctorDto(doctorRepository.save(doctorDb));
    }

    public DoctorDto removeDoctorFromClinic(Long id) {
        Doctor doctorDb = getDoctorDb(id);
        doctorDb.setClinic(null);
        return doctorMapper.toDoctorDto(doctorRepository.save(doctorDb));
    }

    private Doctor getDoctorDb(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
    }

    private Clinic getClinicDb(Long id) {
        return clinicRepository.findById(id).orElseThrow(() -> new ClinicNotFoundException(id));
    }
}
