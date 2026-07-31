package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.Clinic.ClinicDetailsDto;
import com.neqrofukk.medicalclinic.entity.Clinic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DoctorMapper.class)
public interface ClinicDetailsMapper {
    ClinicDetailsDto toClinicDetailsDto(Clinic clinic);
}
