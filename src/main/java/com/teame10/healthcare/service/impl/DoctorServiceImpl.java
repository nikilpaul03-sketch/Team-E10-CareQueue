package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.Doctor;
import com.teame10.healthcare.exception.ResourceNotFoundException;
import com.teame10.healthcare.repository.DoctorRepository;
import com.teame10.healthcare.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    @Override
    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for user id: " + userId));
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllWithDetails();
    }

    @Override
    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue();
    }

    @Override
    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentIdAndAvailableTrue(departmentId);
    }

    @Override
    public Page<Doctor> searchDoctorsBySpecialization(String keyword, Pageable pageable) {
        return doctorRepository.findBySpecializationContainingIgnoreCase(keyword, pageable);
    }

    @Override
    @Transactional
    public Doctor updateDoctor(Long id, Doctor details) {
        Doctor existing = getDoctorById(id);
        existing.setSpecialization(details.getSpecialization());
        existing.setQualification(details.getQualification());
        existing.setExperienceYears(details.getExperienceYears());
        existing.setRoomNumber(details.getRoomNumber());
        existing.setConsultationFee(details.getConsultationFee());
        existing.setAvailable(details.isAvailable());
        if (details.getProfilePhotoPath() != null) {
            existing.setProfilePhotoPath(details.getProfilePhotoPath());
        }
        return doctorRepository.save(existing);
    }

    @Override
    @Transactional
    public void setDoctorAvailability(Long id, boolean available) {
        Doctor doctor = getDoctorById(id);
        doctor.setAvailable(available);
        doctorRepository.save(doctor);
    }
}
