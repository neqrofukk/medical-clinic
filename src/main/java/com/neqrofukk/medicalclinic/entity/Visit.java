package com.neqrofukk.medicalclinic.entity;

import com.neqrofukk.medicalclinic.dto.Visit.VisitCreateCommand;
import com.neqrofukk.medicalclinic.dto.Visit.VisitUpdateCommand;
import com.neqrofukk.medicalclinic.util.Utils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "visits")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name="doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public Visit addVisit(VisitCreateCommand visit, Doctor doctor) {
        Visit visitEntity = new Visit();
        visitEntity.setStartTime(visit.startTime());
        visitEntity.setEndTime(visit.endTime());
        visitEntity.setDoctor(doctor);

        return visitEntity;
    }

    public void updateVisit(VisitUpdateCommand visit, Doctor doctor) {
        Utils.setIfNotNullDateTime(visit.startTime(), this::setStartTime);
        Utils.setIfNotNullDateTime(visit.endTime(), this::setEndTime);
        setDoctor(doctor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visit))
            return false;
        Visit other = (Visit) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
