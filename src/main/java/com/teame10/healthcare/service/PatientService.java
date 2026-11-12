package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {

    Patient registerPatient(Patient patient);

    Patient getPatientById(Long id);

    Patient getPatientByIdentifier(String patientIdentifier);

    Patient getPatientByUserId(Long userId);

    List<Patient> getAllPatients();

    Page<Patient> searchPatients(String query, Pageable pageable);

    Patient updatePatient(Long id, Patient patientDetails);
}
