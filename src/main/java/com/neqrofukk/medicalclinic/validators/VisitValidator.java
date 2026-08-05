package com.neqrofukk.medicalclinic.validators;

import com.neqrofukk.medicalclinic.dto.Visit.VisitValidityCheck;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.Visit;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitNotFutureDateException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitNotQuarterHourException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitOverlapException;
import com.neqrofukk.medicalclinic.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class VisitValidator implements Validator<VisitValidityCheck> {
    
    @Override
    public void validate(VisitValidityCheck check) {
        checkIfOverlaps(check.doctor().getVisit(), check.startTime(), check.endTime(), check.editedVisitId());
        checkIfFutureDate(check);
        checkIfQuarterHour(check);
    }

    public void validatePatientOverlap(Patient patient, LocalDateTime startTime, LocalDateTime endTime, Long editedVisitId) {
        checkIfOverlaps(patient.getVisit(), startTime, endTime, editedVisitId);
    }

    private void checkIfOverlaps(Set<Visit> visits, LocalDateTime startTime, LocalDateTime endTime, Long editedVisitId) {
        boolean overlaps = visits.stream()
                .anyMatch(v -> !v.getId().equals(editedVisitId)
                        && DateUtils.isOverlapOpenEnd(v.getStartTime(), v.getEndTime(), startTime, endTime));

        if (overlaps) {
            throw new VisitOverlapException();
        }
    }

    private void checkIfFutureDate(VisitValidityCheck check) {
        boolean futureDate = check.startTime().isAfter(LocalDateTime.now());

        if (!futureDate) {
            throw new VisitNotFutureDateException();
        }
    }

    private void checkIfQuarterHour(VisitValidityCheck check) {
        boolean startQuarterMinutes = check.startTime().getMinute() % 15 == 0;
        boolean endQuarterMinutes = check.endTime().getMinute() % 15 == 0;

        if (!(startQuarterMinutes && endQuarterMinutes)) {
            throw new VisitNotQuarterHourException();
        }
    }
}
