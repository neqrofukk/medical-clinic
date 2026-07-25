package com.example.clinic.service;

import com.example.clinic.dto.PatientCreateDto;
import com.example.clinic.dto.PatientDto;
import com.example.clinic.dto.PatientUpdateDto;
import com.example.clinic.entity.Patient;
import com.example.clinic.exception.PatientNotFoundException;
import com.example.clinic.exception.PeselAlreadyExistsException;
import com.example.clinic.mapper.PatientMapper;
import com.example.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

//    private final PatientRepository patientRepository;
//    private final PatientMapper patientMapper;
//
//    public List<PatientDto> findAll() {
//        return patientRepository.findAll()
//                .stream()
//                .map(patientMapper::toDto)
//                .toList();
//    }
//
//    public PatientDto findById(Long id) {
//        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
//        return patientMapper.toDto(patient);
//    }
//
//    public PatientDto create(PatientCreateDto dto) {
//        if (patientRepository.existsByPesel(dto.getPesel())) {
//            throw new PeselAlreadyExistsException(dto.getPesel());
//        }
//        Patient patient = patientMapper.toEntity(dto);
//        patient.setCreatedAt(LocalDateTime.now());
//        Patient saved = patientRepository.save(patient);
//        return patientMapper.toDto(saved);
//    }
//
//    public PatientDto update(Long id, PatientUpdateDto dto) {
//        Patient patient = patientRepository.findById(id)
//                .orElseThrow(() -> new PatientNotFoundException(id));
//        patientMapper.updateEntity(patient, dto);
//        return patientMapper.toDto(patientRepository.save(patient));
//    }
//
//    public void delete(Long id) {
//        if (!patientRepository.existsById(id)) {
//            throw new PatientNotFoundException(id);
//        }
//        patientRepository.deleteById(id);
//    }
}
