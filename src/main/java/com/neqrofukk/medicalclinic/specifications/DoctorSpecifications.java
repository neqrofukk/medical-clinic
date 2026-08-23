package com.neqrofukk.medicalclinic.specifications;

import com.neqrofukk.medicalclinic.entity.Doctor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DoctorSpecifications {

    public static Specification<Doctor> hasSpecialty(String specialty) {
        return (root, query, cb) -> specialty == null ? null
                : cb.like(cb.lower(root.get("specialty")), "%" + specialty.toLowerCase() + "%");
    }

    public static Specification<Doctor> build(String specialty) {
        return Specification.allOf(hasSpecialty(specialty));
    }
}
