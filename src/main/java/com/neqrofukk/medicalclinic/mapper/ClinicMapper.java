package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.ClinicCreateCommand;
import com.neqrofukk.medicalclinic.dto.ClinicDto;
import com.neqrofukk.medicalclinic.entity.Clinic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DoctorMapper.class)
public interface ClinicMapper {
    Clinic toEntity(ClinicCreateCommand clinic);
    ClinicDto toClinicDto(Clinic clinic);
}
