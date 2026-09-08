package com.neqrofukk.medicalclinic.validator;

import com.neqrofukk.medicalclinic.dto.Visit.VisitValidityCheck;
import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.entity.Visit;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitNotFutureDateException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitNotQuarterHourException;
import com.neqrofukk.medicalclinic.exceptions.visit.VisitOverlapException;
import com.neqrofukk.medicalclinic.validators.VisitValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VisitValidatorTest {

    VisitValidator visitValidator;
    LocalDateTime futureHourStart;

    @BeforeEach
    void setup() {
        visitValidator = new VisitValidator();
        futureHourStart = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    void validate_FutureHourNoOverlap_DoesNotThrow() {
        Doctor doctor = new Doctor(1L, "shrink", null, null, new HashSet<>(), null);
        VisitValidityCheck check = new VisitValidityCheck(doctor, futureHourStart, futureHourStart.plusMinutes(30), null);

        assertDoesNotThrow(() -> visitValidator.validate(check));
    }

    @Test
    void validate_OverlapsDoctorVisit_ThrowsVisitOverlapException() {
        Visit existingVisit = new Visit(10L, futureHourStart, futureHourStart.plusMinutes(30), null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", null, null, new HashSet<>(Set.of(existingVisit)), null);
        VisitValidityCheck check = new VisitValidityCheck(doctor, futureHourStart.plusMinutes(15), futureHourStart.plusMinutes(45), null);

        assertThrows(VisitOverlapException.class, () -> visitValidator.validate(check));
    }

    @Test
    void validate_TouchingButNotOverlappingDoctorVisit_DoesNotThrow() {
        Visit existingVisit = new Visit(10L, futureHourStart, futureHourStart.plusMinutes(30), null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", null, null, new HashSet<>(Set.of(existingVisit)), null);
        VisitValidityCheck check = new VisitValidityCheck(doctor, futureHourStart.plusMinutes(30), futureHourStart.plusMinutes(60), null);

        assertDoesNotThrow(() -> visitValidator.validate(check));
    }

    @Test
    void validate_OverlapCheckExcludesVisitBeingEdited_DoesNotThrow() {
        Visit existingVisit = new Visit(10L, futureHourStart, futureHourStart.plusMinutes(30), null, null, null);
        Doctor doctor = new Doctor(1L, "shrink", null, null, new HashSet<>(Set.of(existingVisit)), null);
        VisitValidityCheck check = new VisitValidityCheck(doctor, futureHourStart.plusMinutes(15), futureHourStart.plusMinutes(45), 10L);

        assertDoesNotThrow(() -> visitValidator.validate(check));
    }

    @Test
    void validate_PastStartDate_ThrowsVisitNotFutureDateException() {
        Doctor doctor = new Doctor(1L, "shrink", null, null, new HashSet<>(), null);
        LocalDateTime pastStart = LocalDateTime.now().minusDays(1);
        VisitValidityCheck check = new VisitValidityCheck(doctor, pastStart, pastStart.plusMinutes(30), null);

        assertThrows(VisitNotFutureDateException.class, () -> visitValidator.validate(check));
    }

    @Test
    void validate_StartTimeNotQuarterHour_ThrowsVisitNotQuarterHourException() {
        Doctor doctor = new Doctor(1L, "shrink", null, null, new HashSet<>(), null);
        LocalDateTime start = futureHourStart.plusMinutes(7);
        VisitValidityCheck check = new VisitValidityCheck(doctor, start, start.plusMinutes(30), null);

        assertThrows(VisitNotQuarterHourException.class, () -> visitValidator.validate(check));
    }

    @Test
    void validate_EndTimeNotQuarterHour_ThrowsVisitNotQuarterHourException() {
        Doctor doctor = new Doctor(1L, "shrink", new HashSet<>(), null, new HashSet<>(), null);
        VisitValidityCheck check = new VisitValidityCheck(doctor, futureHourStart, futureHourStart.plusMinutes(22), null);

        assertThrows(VisitNotQuarterHourException.class, () -> visitValidator.validate(check));
    }

    @Test
    void validatePatientOverlap_NoOverlap_DoesNotThrow() {
        Patient patient = new Patient(1L, "123456", null, null, null, new HashSet<>(), null);

        assertDoesNotThrow(() -> visitValidator.validatePatientOverlap(
                patient, futureHourStart, futureHourStart.plusMinutes(30), null));
    }

    @Test
    void validatePatientOverlap_OverlapsPatientVisit_ThrowsVisitOverlapException() {
        Visit existingVisit = new Visit(20L, futureHourStart, futureHourStart.plusMinutes(30), null, null, null);
        Patient patient = new Patient(1L, "123456", null, null, null, new HashSet<>(Set.of(existingVisit)), null);

        assertThrows(VisitOverlapException.class, () -> visitValidator.validatePatientOverlap(
                patient, futureHourStart.plusMinutes(15), futureHourStart.plusMinutes(45), null));
    }

    @Test
    void validatePatientOverlap_CheckExcludesTheVisitBeingEdited_DoesNotThrow() {
        Visit existingVisit = new Visit(20L, futureHourStart, futureHourStart.plusMinutes(30), null, null, null);
        Patient patient = new Patient(1L, "123456", null, null, null, new HashSet<>(Set.of(existingVisit)), null);

        assertDoesNotThrow(() -> visitValidator.validatePatientOverlap(
                patient, futureHourStart.plusMinutes(15), futureHourStart.plusMinutes(45), 20L));
    }
}