package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.Appointment;
import com.teame10.healthcare.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AppointmentService {

    Appointment bookAppointment(Long patientId, Long doctorId, Long departmentId, LocalDate appointmentDate, LocalTime appointmentTime, String reason);

    Appointment rescheduleAppointment(Long appointmentId, LocalDate newDate, LocalTime newTime, String reason);

    Appointment cancelAppointment(Long appointmentId, String cancellationReason);

    Appointment markNoShow(Long appointmentId, String reason);

    Appointment checkIn(Long appointmentId);

    Appointment completeAppointment(Long appointmentId);

    Appointment getAppointmentById(Long id);

    Appointment getAppointmentByReference(String reference);

    List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date);

    List<Appointment> getAppointmentsByPatient(Long patientId);

    Page<Appointment> getAppointmentsByPatientPaged(Long patientId, Pageable pageable);

    Page<Appointment> getFilteredAppointments(Long doctorId, Long departmentId, AppointmentStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Map<AppointmentStatus, Long> getAppointmentStatusCounts();
}
