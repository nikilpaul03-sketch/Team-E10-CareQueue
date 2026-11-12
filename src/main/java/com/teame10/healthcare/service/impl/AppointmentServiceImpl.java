package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.*;
import com.teame10.healthcare.exception.*;
import com.teame10.healthcare.repository.*;
import com.teame10.healthcare.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorScheduleRepository scheduleRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  DepartmentRepository departmentRepository,
                                  DoctorScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    @Transactional
    public Appointment bookAppointment(Long patientId, Long doctorId, Long departmentId,
                                       LocalDate appointmentDate, LocalTime appointmentTime, String reason) {
        // 1. Verify Patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // 2. Verify Doctor exists and is available
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        if (!doctor.isAvailable()) {
            throw new SlotNotAvailableException("Doctor is currently not available for consultation.");
        }

        // 3. Verify Department exists and matches doctor
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        // 4. Validate appointment is not in the past
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book appointment for a past date.");
        }

        // 5. Validate Doctor Schedule on that day of week
        DayOfWeek day = appointmentDate.getDayOfWeek();
        DoctorSchedule schedule = scheduleRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(doctorId, day)
                .orElseThrow(() -> new SlotNotAvailableException("Doctor " + doctor.getUser().getFullName() + " has no clinic session on " + day));

        if (appointmentTime.isBefore(schedule.getSessionStartTime()) || appointmentTime.isAfter(schedule.getSessionEndTime())) {
            throw new SlotNotAvailableException("Selected time " + appointmentTime + " is outside the doctor's clinic hours (" +
                    schedule.getSessionStartTime() + " - " + schedule.getSessionEndTime() + ").");
        }

        // 6. Check slot capacity for the day
        long bookedCount = appointmentRepository.countByDoctorIdAndAppointmentDateAndStatusNot(doctorId, appointmentDate, AppointmentStatus.CANCELLED);
        if (bookedCount >= schedule.getSlotCapacity()) {
            throw new SlotNotAvailableException("Doctor's session is fully booked for " + appointmentDate + ". Capacity: " + schedule.getSlotCapacity());
        }

        // 7. Check Duplicate Booking (Anti-Collision check)
        if (appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(doctorId, appointmentDate, appointmentTime)) {
            throw new DuplicateBookingException("A booking already exists for Dr. " + doctor.getUser().getFullName() +
                    " on " + appointmentDate + " at " + appointmentTime);
        }

        // 8. Create and persist Appointment with unique reference
        String reference = generateAppointmentReference(appointmentDate);
        Appointment appointment = new Appointment(reference, patient, doctor, department, appointmentDate, appointmentTime);
        appointment.setReasonForVisit(reason);
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment rescheduleAppointment(Long appointmentId, LocalDate newDate, LocalTime newTime, String reason) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot reschedule an appointment that has already been completed.");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot reschedule a cancelled appointment.");
        }

        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot reschedule appointment to a past date.");
        }

        // Check duplicate booking on new slot
        if (appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(appointment.getDoctor().getId(), newDate, newTime)) {
            throw new DuplicateBookingException("Requested slot on " + newDate + " at " + newTime + " is already reserved.");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setReasonForVisit(reason != null ? reason : appointment.getReasonForVisit());

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment cancelAppointment(Long appointmentId, String cancellationReason) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot cancel an appointment that has already been completed.");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidOperationException("Appointment is already cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancellationReason != null ? cancellationReason : "Cancelled by user/patient");
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment markNoShow(Long appointmentId, String reason) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot mark a completed appointment as no-show.");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot mark a cancelled appointment as no-show.");
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment.setCancellationReason(reason != null ? reason : "Patient did not arrive during scheduled OPD hours");
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment checkIn(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot check in an appointment with status: " + appointment.getStatus());
        }
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment completeAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
    }

    @Override
    public Appointment getAppointmentByReference(String reference) {
        return appointmentRepository.findByAppointmentReference(reference)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with reference: " + reference));
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public Page<Appointment> getAppointmentsByPatientPaged(Long patientId, Pageable pageable) {
        return appointmentRepository.findByPatientId(patientId, pageable);
    }

    @Override
    public Page<Appointment> getFilteredAppointments(Long doctorId, Long departmentId, AppointmentStatus status,
                                                     LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return appointmentRepository.findFilteredAppointments(doctorId, departmentId, status, startDate, endDate, pageable);
    }

    @Override
    public Map<AppointmentStatus, Long> getAppointmentStatusCounts() {
        Map<AppointmentStatus, Long> counts = new EnumMap<>(AppointmentStatus.class);
        for (AppointmentStatus status : AppointmentStatus.values()) {
            counts.put(status, appointmentRepository.countByStatus(status));
        }
        return counts;
    }

    private String generateAppointmentReference(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String randStr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "APT-" + dateStr + "-" + randStr;
    }
}
