package com.teame10.healthcare.repository;

import com.teame10.healthcare.entity.Appointment;
import com.teame10.healthcare.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentReference(String appointmentReference);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    List<Appointment> findByPatientId(Long patientId);

    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatus(Long doctorId, LocalDate appointmentDate, AppointmentStatus status);

    long countByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate appointmentDate, AppointmentStatus status);

    Page<Appointment> findByAppointmentDate(LocalDate appointmentDate, Pageable pageable);

    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    long countByStatus(AppointmentStatus status);

    long countByAppointmentDate(LocalDate appointmentDate);

    @Query("SELECT a FROM Appointment a " +
           "WHERE (:doctorId IS NULL OR a.doctor.id = :doctorId) " +
           "AND (:departmentId IS NULL OR a.department.id = :departmentId) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:startDate IS NULL OR a.appointmentDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.appointmentDate <= :endDate)")
    Page<Appointment> findFilteredAppointments(
            @Param("doctorId") Long doctorId,
            @Param("departmentId") Long departmentId,
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
