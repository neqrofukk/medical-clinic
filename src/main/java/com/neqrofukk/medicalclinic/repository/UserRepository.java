package com.neqrofukk.medicalclinic.repository;

import com.neqrofukk.medicalclinic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
