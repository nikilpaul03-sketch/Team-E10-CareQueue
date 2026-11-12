package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.Patient;
import com.teame10.healthcare.exception.ResourceNotFoundException;
import com.teame10.healthcare.repository.PatientRepository;
import com.teame10.healthcare.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional
    public Patient registerPatient(Patient patient) {
        if (patient.getPatientIdentifier() == null || patient.getPatientIdentifier().isBlank()) {
            patient.setPatientIdentifier(generatePatientIdentifier());
        } else if (patientRepository.existsByPatientIdentifier(patient.getPatientIdentifier())) {
            throw new IllegalArgumentException("Patient identifier already exists: " + patient.getPatientIdentifier());
        }
        return patientRepository.save(patient);
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    public Patient getPatientByIdentifier(String patientIdentifier) {
        return patientRepository.findByPatientIdentifier(patientIdentifier)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with identifier: " + patientIdentifier));
    }

    @Override
    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user id: " + userId));
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Page<Patient> searchPatients(String query, Pageable pageable) {
        String safeQuery = query == null ? "" : query.trim();
        return patientRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrPhoneNumberContaining(
                safeQuery, safeQuery, safeQuery, pageable);
    }

    @Override
    @Transactional
    public Patient updatePatient(Long id, Patient details) {
        Patient existing = getPatientById(id);
        existing.setFirstName(details.getFirstName());
        existing.setLastName(details.getLastName());
        existing.setDateOfBirth(details.getDateOfBirth());
        existing.setGender(details.getGender());
        existing.setPhoneNumber(details.getPhoneNumber());
        existing.setEmail(details.getEmail());
        existing.setAddress(details.getAddress());
        existing.setBloodGroup(details.getBloodGroup());
        existing.setEmergencyContactName(details.getEmergencyContactName());
        existing.setEmergencyContactPhone(details.getEmergencyContactPhone());
        return patientRepository.save(existing);
    }

    private String generatePatientIdentifier() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "PAT-" + datePart + "-" + randomPart;
    }
}
