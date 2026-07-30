package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.DoctorCreateCommand;
import com.neqrofukk.medicalclinic.dto.DoctorDto;
import com.neqrofukk.medicalclinic.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    Doctor toEntity(DoctorCreateCommand doctor);
    @Mapping(target = "clinicId", source = "clinic.id")
    DoctorDto toDoctorDto(Doctor doctor);
}
