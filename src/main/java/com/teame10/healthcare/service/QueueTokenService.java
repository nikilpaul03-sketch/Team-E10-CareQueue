package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.QueueToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QueueTokenService {

    QueueToken generateTokenForAppointment(Long appointmentId);

    Optional<QueueToken> callNextToken(Long doctorId, LocalDate tokenDate, String counterRoom);

    QueueToken startServingToken(Long tokenId);

    QueueToken completeToken(Long tokenId);

    QueueToken skipToken(Long tokenId);

    QueueToken getTokenById(Long id);

    QueueToken getTokenByAppointmentId(Long appointmentId);

    List<QueueToken> getTokensForDoctorToday(Long doctorId, LocalDate tokenDate);

    List<QueueToken> getActiveTokensForDoctor(Long doctorId, LocalDate tokenDate);
}
