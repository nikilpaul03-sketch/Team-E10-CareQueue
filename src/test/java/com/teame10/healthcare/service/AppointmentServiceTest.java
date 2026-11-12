package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.*;
import com.teame10.healthcare.exception.DuplicateBookingException;
import com.teame10.healthcare.exception.InvalidOperationException;
import com.teame10.healthcare.exception.SlotNotAvailableException;
import com.teame10.healthcare.repository.*;
import com.teame10.healthcare.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DoctorScheduleRepository scheduleRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient testPatient;
    private Doctor testDoctor;
    private Department testDept;
    private DoctorSchedule testSchedule;

    @BeforeEach
    void setUp() {
        User docUser = new User("dr_smith", "pass123", "smith@hospital.org", "Dr. John Smith", "9876543210", Role.DOCTOR);
        testDept = new Department("General Medicine", "GEN", "OPD", "Room 101");
        testDoctor = new Doctor(docUser, testDept, "General Physician", "MD", 10, "101");
        testDoctor.setId(1L);

        testPatient = new Patient("PAT-1001", "Alice", "Brown", "9123456780");
        testPatient.setId(10L);

        // Schedule: Monday to Friday 09:00 - 17:00, capacity 20
        testSchedule = new DoctorSchedule(testDoctor, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), 15, 20);
    }

    @Test
    @DisplayName("Should successfully book an appointment when slot is available")
    void testBookAppointmentSuccess() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        LocalTime slotTime = LocalTime.of(10, 0);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(departmentRepository.findById(testDept.getId())).thenReturn(Optional.of(testDept));
        when(scheduleRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(1L, futureDate.getDayOfWeek()))
                .thenReturn(Optional.of(testSchedule));
        when(appointmentRepository.countByDoctorIdAndAppointmentDateAndStatusNot(1L, futureDate, AppointmentStatus.CANCELLED))
                .thenReturn(5L);
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(1L, futureDate, slotTime))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment booked = appointmentService.bookAppointment(10L, 1L, testDept.getId(), futureDate, slotTime, "Routine Checkup");

        assertThat(booked).isNotNull();
        assertThat(booked.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(booked.getAppointmentReference()).startsWith("APT-");
        assertThat(booked.getPatient()).isEqualTo(testPatient);
        assertThat(booked.getDoctor()).isEqualTo(testDoctor);
    }

    @Test
    @DisplayName("Should throw DuplicateBookingException when slot is already reserved")
    void testBookAppointmentDuplicateSlot() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        LocalTime slotTime = LocalTime.of(10, 0);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(departmentRepository.findById(testDept.getId())).thenReturn(Optional.of(testDept));
        when(scheduleRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(1L, futureDate.getDayOfWeek()))
                .thenReturn(Optional.of(testSchedule));
        when(appointmentRepository.countByDoctorIdAndAppointmentDateAndStatusNot(1L, futureDate, AppointmentStatus.CANCELLED))
                .thenReturn(5L);
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(1L, futureDate, slotTime))
                .thenReturn(true);

        assertThatThrownBy(() -> appointmentService.bookAppointment(10L, 1L, testDept.getId(), futureDate, slotTime, "Follow-up"))
                .isInstanceOf(DuplicateBookingException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should throw SlotNotAvailableException when time is outside clinic hours")
    void testBookAppointmentOutsideHours() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        LocalTime outsideTime = LocalTime.of(19, 0); // Outside 09:00 - 17:00

        when(patientRepository.findById(10L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(departmentRepository.findById(testDept.getId())).thenReturn(Optional.of(testDept));
        when(scheduleRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(1L, futureDate.getDayOfWeek()))
                .thenReturn(Optional.of(testSchedule));

        assertThatThrownBy(() -> appointmentService.bookAppointment(10L, 1L, testDept.getId(), futureDate, outsideTime, "Checkup"))
                .isInstanceOf(SlotNotAvailableException.class)
                .hasMessageContaining("outside the doctor's clinic hours");
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void testCancelAppointment() {
        Appointment apt = new Appointment("APT-REF-01", testPatient, testDoctor, testDept, LocalDate.now().plusDays(2), LocalTime.of(11, 0));
        apt.setId(100L);
        apt.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(apt));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment cancelled = appointmentService.cancelAppointment(100L, "Patient unable to attend");

        assertThat(cancelled.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(cancelled.getCancellationReason()).isEqualTo("Patient unable to attend");
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when attempting to cancel completed appointment")
    void testCannotCancelCompletedAppointment() {
        Appointment apt = new Appointment("APT-REF-02", testPatient, testDoctor, testDept, LocalDate.now(), LocalTime.of(9, 0));
        apt.setId(101L);
        apt.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(101L)).thenReturn(Optional.of(apt));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(101L, "Cancel"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("already been completed");
    }

    @Test
    @DisplayName("Should mark eligible appointment as no-show")
    void testMarkNoShow() {
        Appointment apt = new Appointment("APT-REF-03", testPatient, testDoctor, testDept, LocalDate.now(), LocalTime.of(9, 30));
        apt.setId(102L);
        apt.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepository.findById(102L)).thenReturn(Optional.of(apt));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment noShow = appointmentService.markNoShow(102L, "Did not arrive");

        assertThat(noShow.getStatus()).isEqualTo(AppointmentStatus.NO_SHOW);
    }
}
