package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.Doctor.DoctorDetailsDto;
import com.neqrofukk.medicalclinic.entity.Doctor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ClinicMapper.class)
public interface DoctorDetailsMapper {
    DoctorDetailsDto toDoctorDetailsDto(Doctor doctor);
}
