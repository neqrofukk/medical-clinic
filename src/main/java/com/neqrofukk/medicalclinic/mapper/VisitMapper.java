package com.neqrofukk.medicalclinic.mapper;

import com.neqrofukk.medicalclinic.dto.Visit.VisitCreateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.entity.Visit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    Visit toEntity(VisitCreateCommand visit);
    VisitDto toVisitDto(Visit visit);
}
