package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.*;
import com.teame10.healthcare.exception.QueueTokenException;
import com.teame10.healthcare.exception.ResourceNotFoundException;
import com.teame10.healthcare.repository.AppointmentRepository;
import com.teame10.healthcare.repository.QueueTokenRepository;
import com.teame10.healthcare.service.QueueTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class QueueTokenServiceImpl implements QueueTokenService {

    private final QueueTokenRepository tokenRepository;
    private final AppointmentRepository appointmentRepository;

    public QueueTokenServiceImpl(QueueTokenRepository tokenRepository,
                                 AppointmentRepository appointmentRepository) {
        this.tokenRepository = tokenRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public QueueToken generateTokenForAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        if (tokenRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new QueueTokenException("A queue token has already been generated for appointment id: " + appointmentId);
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new QueueTokenException("Cannot generate token for appointment with status: " + appointment.getStatus());
        }

        Doctor doctor = appointment.getDoctor();
        Department department = appointment.getDepartment();
        LocalDate tokenDate = appointment.getAppointmentDate();

        // Calculate next sequential token index for this doctor today
        long currentCount = tokenRepository.countByDoctorIdAndTokenDate(doctor.getId(), tokenDate);
        int nextSeq = (int) (currentCount + 1);
        String tokenNumber = department.getCode() + "-" + String.format("%03d", nextSeq);

        QueueToken token = new QueueToken(tokenNumber, tokenDate, appointment, doctor, department, nextSeq);
        token.setCounterRoom(doctor.getRoomNumber());
        token.setStatus(TokenStatus.GENERATED);

        // Update appointment status to CHECKED_IN
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Optional<QueueToken> callNextToken(Long doctorId, LocalDate tokenDate, String counterRoom) {
        Optional<QueueToken> nextTokenOpt = tokenRepository.findFirstByDoctorIdAndTokenDateAndStatusOrderByQueuePositionAsc(
                doctorId, tokenDate, TokenStatus.GENERATED);

        if (nextTokenOpt.isPresent()) {
            QueueToken token = nextTokenOpt.get();
            token.setStatus(TokenStatus.CALLING);
            token.setCalledAt(LocalDateTime.now());
            if (counterRoom != null && !counterRoom.isBlank()) {
                token.setCounterRoom(counterRoom);
            }
            return Optional.of(tokenRepository.save(token));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public QueueToken startServingToken(Long tokenId) {
        QueueToken token = getTokenById(tokenId);
        token.setStatus(TokenStatus.SERVING);
        Appointment appointment = token.getAppointment();
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.IN_CONSULTATION);
            appointmentRepository.save(appointment);
        }
        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public QueueToken completeToken(Long tokenId) {
        QueueToken token = getTokenById(tokenId);
        token.setStatus(TokenStatus.COMPLETED);
        token.setCompletedAt(LocalDateTime.now());

        Appointment appointment = token.getAppointment();
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(appointment);
        }
        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public QueueToken skipToken(Long tokenId) {
        QueueToken token = getTokenById(tokenId);
        token.setStatus(TokenStatus.SKIPPED);
        return tokenRepository.save(token);
    }

    @Override
    public QueueToken getTokenById(Long id) {
        return tokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue token not found with id: " + id));
    }

    @Override
    public QueueToken getTokenByAppointmentId(Long appointmentId) {
        return tokenRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue token not found for appointment id: " + appointmentId));
    }

    @Override
    public List<QueueToken> getTokensForDoctorToday(Long doctorId, LocalDate tokenDate) {
        return tokenRepository.findByDoctorIdAndTokenDateOrderByQueuePositionAsc(doctorId, tokenDate);
    }

    @Override
    public List<QueueToken> getActiveTokensForDoctor(Long doctorId, LocalDate tokenDate) {
        return tokenRepository.findByDoctorIdAndTokenDateAndStatus(doctorId, tokenDate, TokenStatus.GENERATED);
    }
}
