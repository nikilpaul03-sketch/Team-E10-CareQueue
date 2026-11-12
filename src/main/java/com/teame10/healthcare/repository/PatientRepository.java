package com.teame10.healthcare.repository;

import com.teame10.healthcare.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientIdentifier(String patientIdentifier);

    Optional<Patient> findByUserId(Long userId);

    Optional<Patient> findByPhoneNumber(String phoneNumber);

    boolean existsByPatientIdentifier(String patientIdentifier);

    Page<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrPhoneNumberContaining(
            String lastName, String firstName, String phoneNumber, Pageable pageable);
}
