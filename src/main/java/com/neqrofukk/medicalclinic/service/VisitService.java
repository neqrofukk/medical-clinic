package com.neqrofukk.medicalclinic.service;

import com.neqrofukk.medicalclinic.dto.PageResponse;
import com.neqrofukk.medicalclinic.dto.Visit.VisitCreateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitDto;
import com.neqrofukk.medicalclinic.dto.Visit.VisitUpdateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitValidityCheck;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.Visit;
import com.neqrofukk.medicalclinic.exceptions.DoctorNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitAlreadyTakenException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitNotFoundException;
import com.neqrofukk.medicalclinic.mapper.VisitMapper;
import com.neqrofukk.medicalclinic.repository.DoctorRepository;
import com.neqrofukk.medicalclinic.repository.PatientRepository;
import com.neqrofukk.medicalclinic.repository.VisitRepository;
import com.neqrofukk.medicalclinic.validators.SortValidator;
import com.neqrofukk.medicalclinic.validators.VisitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class VisitService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "startTime", "endTime", "doctorId", "patientId");

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final VisitValidator visitValidator;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public PageResponse<VisitDto> getVisits(Pageable pageable) {
        SortValidator.validate(pageable.getSort(), ALLOWED_SORT_FIELDS);

        Page<Visit> page = visitRepository.findAll(pageable);
        return PageResponse.from(page.map(visitMapper::toVisitDto));
    }

    @Transactional(readOnly = true)
    public VisitDto findById(Long id) {
        Visit visitDb = getVisitDb(id);
        return visitMapper.toVisitDto(visitDb);
    }

    @Transactional
    public VisitDto addVisit(VisitCreateCommand visit) {
        Doctor doctorDb = getDoctorDb(visit.doctorId());

        VisitValidityCheck check = new VisitValidityCheck(doctorDb, visit.startTime(), visit.endTime(), null);
        visitValidator.validate(check);

        Visit visitEntity = (new Visit()).addVisit(visit, doctorDb);
        Visit visitDb = visitRepository.save(visitEntity);
        return visitMapper.toVisitDto(visitDb);
    }

    @Transactional
    public VisitDto updateVisit(Long id, VisitUpdateCommand visit) {
        Visit visitDb = getVisitDb(id);
        Doctor doctorDb = visit.doctorId() != null ? getDoctorDb(visit.doctorId()) : visitDb.getDoctor();
        visitDb.updateVisit(visit, doctorDb);

        VisitValidityCheck check = new VisitValidityCheck(doctorDb, visit.startTime(), visit.endTime(), id);
        visitValidator.validate(check);

        visitRepository.save(visitDb);
        return visitMapper.toVisitDto(visitDb);
    }

    @Transactional
    public void deleteVisit(Long id) {
        visitRepository.deleteById(id);
    }

    @Transactional
    public VisitDto addPatientToVisit(Long visitId, Long patientId) {
        Visit visit = getVisitDb(visitId);
        Patient patientDb = getPatientDb(patientId);

        if (visit.getPatient() != null) {
            throw new VisitAlreadyTakenException(visitId);
        }

        visitValidator.validatePatientOverlap(patientDb, visit.getStartTime(), visit.getEndTime(), visitId);

        visit.setPatient(patientDb);
        return visitMapper.toVisitDto(visitRepository.save(visit));
    }

    @Transactional
    public VisitDto removePatientFromVisit(Long visitId) {
        Visit visit = getVisitDb(visitId);
        visit.setPatient(null);
        return visitMapper.toVisitDto(visitRepository.save(visit));
    }
    
    private Visit getVisitDb(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(id));
    }

    private Patient getPatientDb(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    private Doctor getDoctorDb(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }
}
