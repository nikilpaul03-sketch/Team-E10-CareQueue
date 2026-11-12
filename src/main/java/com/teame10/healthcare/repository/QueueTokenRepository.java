package com.teame10.healthcare.repository;

import com.teame10.healthcare.entity.QueueToken;
import com.teame10.healthcare.entity.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

    Optional<QueueToken> findByAppointmentId(Long appointmentId);

    Optional<QueueToken> findByDoctorIdAndTokenDateAndTokenNumber(Long doctorId, LocalDate tokenDate, String tokenNumber);

    List<QueueToken> findByDoctorIdAndTokenDate(Long doctorId, LocalDate tokenDate);

    List<QueueToken> findByDoctorIdAndTokenDateOrderByQueuePositionAsc(Long doctorId, LocalDate tokenDate);

    List<QueueToken> findByDoctorIdAndTokenDateAndStatus(Long doctorId, LocalDate tokenDate, TokenStatus status);

    long countByDoctorIdAndTokenDate(Long doctorId, LocalDate tokenDate);

    Optional<QueueToken> findFirstByDoctorIdAndTokenDateAndStatusOrderByQueuePositionAsc(Long doctorId, LocalDate tokenDate, TokenStatus status);
}
