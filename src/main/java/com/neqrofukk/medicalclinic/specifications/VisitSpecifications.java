package com.neqrofukk.medicalclinic.specifications;

import com.neqrofukk.medicalclinic.entity.Doctor;
import com.neqrofukk.medicalclinic.entity.Visit;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisitSpecifications {

    public static Specification<Visit> hasSpecialty(String specialty) {
        return (root, query, cb) -> {
            Join<Visit, Doctor> visitDoctorJoin = root.join("doctor", JoinType.LEFT);
            return specialty == null ? null : cb.like(cb.lower(visitDoctorJoin.get("specialty")), "%" + specialty.toLowerCase() + "%");
        };
    }

    public static Specification<Visit> startsAt(LocalDateTime startTime) {
        return (root, query, cb) -> startTime == null ? null : cb.greaterThanOrEqualTo(root.get("startTime"), startTime);
    }

    public static Specification<Visit> endsAt(LocalDateTime endTime) {
        return (root, query, cb) -> endTime == null ? null : cb.lessThanOrEqualTo(root.get("endTime"), endTime);
    }

    public static Specification<Visit> build(String specialty, LocalDateTime startTime, LocalDateTime endTime) {
        return Specification.allOf(
                hasSpecialty(specialty),
                startsAt(startTime),
                endsAt(endTime));
    }
}
