package com.neqrofukk.medicalclinic.repository;

import com.neqrofukk.medicalclinic.entity.Patient;
import com.neqrofukk.medicalclinic.exceptions.PatientAlreadyExistsException;
import com.neqrofukk.medicalclinic.exceptions.PatientNotFoundException;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPatientRepository implements PatientRepository {
    private final Map<String, Patient> repository = new ConcurrentHashMap<>();

    @Override
    public Patient create(@NonNull Patient patient) {
        Patient existing = repository.putIfAbsent(patient.getEmail(), patient);
        if (existing != null) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }
        return patient;
    }

    @Override
    public void delete(@NonNull String email) {
        if (!repository.containsKey(email)) {
            throw new PatientNotFoundException(email);
        }
        repository.remove(email);
    }

    @Override
    public Optional<Patient> findByEmail(@NonNull String email) {
        return Optional.ofNullable(repository.get(email));
    }

    @Override
    public List<Patient> findAll() {
        return repository.values().stream().toList();
    }

    @Override
    public Patient update(@NonNull String email, @NonNull Patient patient) {
        repository.put(email, patient);
        return repository.get(email);
    }

}
