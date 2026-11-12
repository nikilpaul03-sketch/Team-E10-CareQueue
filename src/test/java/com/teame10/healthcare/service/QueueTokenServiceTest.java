package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.*;
import com.teame10.healthcare.exception.QueueTokenException;
import com.teame10.healthcare.repository.AppointmentRepository;
import com.teame10.healthcare.repository.QueueTokenRepository;
import com.teame10.healthcare.service.impl.QueueTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceTest {

    @Mock
    private QueueTokenRepository tokenRepository;
    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private QueueTokenServiceImpl queueTokenService;

    private Appointment testAppointment;
    private Doctor testDoctor;
    private Department testDept;

    @BeforeEach
    void setUp() {
        User docUser = new User("dr_jones", "pass123", "jones@hospital.org", "Dr. Jones", "9812345678", Role.DOCTOR);
        testDept = new Department("Cardiology", "CARD", "Heart OPD", "Room 201");
        testDoctor = new Doctor(docUser, testDept, "Cardiologist", "MD", 12, "201");
        testDoctor.setId(2L);

        Patient patient = new Patient("PAT-2002", "Robert", "Green", "9876501234");
        patient.setId(20L);

        testAppointment = new Appointment("APT-20261110-002", patient, testDoctor, testDept, LocalDate.now(), LocalTime.of(10, 30));
        testAppointment.setId(50L);
        testAppointment.setStatus(AppointmentStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should successfully generate a digital token and update appointment status to CHECKED_IN")
    void testGenerateTokenSuccess() {
        when(appointmentRepository.findById(50L)).thenReturn(Optional.of(testAppointment));
        when(tokenRepository.findByAppointmentId(50L)).thenReturn(Optional.empty());
        when(tokenRepository.countByDoctorIdAndTokenDate(2L, testAppointment.getAppointmentDate())).thenReturn(3L);
        when(tokenRepository.save(any(QueueToken.class))).thenAnswer(inv -> inv.getArgument(0));

        QueueToken token = queueTokenService.generateTokenForAppointment(50L);

        assertThat(token).isNotNull();
        assertThat(token.getTokenNumber()).isEqualTo("CARD-004");
        assertThat(token.getQueuePosition()).isEqualTo(4);
        assertThat(token.getStatus()).isEqualTo(TokenStatus.GENERATED);
        assertThat(testAppointment.getStatus()).isEqualTo(AppointmentStatus.CHECKED_IN);
    }

    @Test
    @DisplayName("Should reject token generation if token already exists for appointment")
    void testGenerateTokenDuplicate() {
        QueueToken existing = new QueueToken("CARD-001", LocalDate.now(), testAppointment, testDoctor, testDept, 1);
        when(appointmentRepository.findById(50L)).thenReturn(Optional.of(testAppointment));
        when(tokenRepository.findByAppointmentId(50L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> queueTokenService.generateTokenForAppointment(50L))
                .isInstanceOf(QueueTokenException.class)
                .hasMessageContaining("already been generated");
    }

    @Test
    @DisplayName("Should call next token in line and set status to CALLING")
    void testCallNextToken() {
        LocalDate today = LocalDate.now();
        QueueToken waiting = new QueueToken("CARD-001", today, testAppointment, testDoctor, testDept, 1);
        waiting.setStatus(TokenStatus.GENERATED);

        when(tokenRepository.findFirstByDoctorIdAndTokenDateAndStatusOrderByQueuePositionAsc(2L, today, TokenStatus.GENERATED))
                .thenReturn(Optional.of(waiting));
        when(tokenRepository.save(any(QueueToken.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<QueueToken> called = queueTokenService.callNextToken(2L, today, "Room 201");

        assertThat(called).isPresent();
        assertThat(called.get().getStatus()).isEqualTo(TokenStatus.CALLING);
        assertThat(called.get().getCalledAt()).isNotNull();
    }

    @Test
    @DisplayName("Should complete token and mark appointment completed")
    void testCompleteToken() {
        QueueToken token = new QueueToken("CARD-001", LocalDate.now(), testAppointment, testDoctor, testDept, 1);
        token.setId(70L);
        token.setStatus(TokenStatus.SERVING);

        when(tokenRepository.findById(70L)).thenReturn(Optional.of(token));
        when(tokenRepository.save(any(QueueToken.class))).thenAnswer(inv -> inv.getArgument(0));

        QueueToken completed = queueTokenService.completeToken(70L);

        assertThat(completed.getStatus()).isEqualTo(TokenStatus.COMPLETED);
        assertThat(completed.getCompletedAt()).isNotNull();
        assertThat(testAppointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }
}
