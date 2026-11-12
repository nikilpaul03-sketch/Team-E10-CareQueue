package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    Doctor createDoctor(Doctor doctor);

    Doctor getDoctorById(Long id);

    Doctor getDoctorByUserId(Long userId);

    List<Doctor> getAllDoctors();

    List<Doctor> getAvailableDoctors();

    List<Doctor> getDoctorsByDepartment(Long departmentId);

    Page<Doctor> searchDoctorsBySpecialization(String keyword, Pageable pageable);

    Doctor updateDoctor(Long id, Doctor doctorDetails);

    void setDoctorAvailability(Long id, boolean available);
}
