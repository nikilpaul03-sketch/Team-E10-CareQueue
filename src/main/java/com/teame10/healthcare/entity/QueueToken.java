package com.teame10.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Team E10 - QueueToken Entity
 * Represents digital tokens issued for outpatient check-in and queue progression.
 * Includes a database uniqueness constraint for unique daily tokens per doctor.
 */
@Entity
@Table(name = "queue_tokens",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_doctor_daily_token", columnNames = {"doctor_id", "token_date", "token_number"})
    },
    indexes = {
        @Index(name = "idx_token_doctor", columnList = "doctor_id"),
        @Index(name = "idx_token_date", columnList = "token_date"),
        @Index(name = "idx_token_status", columnList = "status"),
        @Index(name = "idx_token_number", columnList = "token_number")
    }
)
public class QueueToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Token number is required")
    @Size(max = 20)
    @Column(name = "token_number", nullable = false, length = 20)
    private String tokenNumber;

    @NotNull(message = "Token date is required")
    @Column(name = "token_date", nullable = false)
    private LocalDate tokenDate;

    @NotNull(message = "Appointment reference is required")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @NotNull(message = "Doctor is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotNull(message = "Department is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull(message = "Token status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenStatus status = TokenStatus.GENERATED;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Size(max = 50)
    @Column(name = "counter_room", length = 50)
    private String counterRoom;

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public QueueToken() {}

    public QueueToken(String tokenNumber, LocalDate tokenDate, Appointment appointment, Doctor doctor, Department department, Integer queuePosition) {
        this.tokenNumber = tokenNumber;
        this.tokenDate = tokenDate;
        this.appointment = appointment;
        this.doctor = doctor;
        this.department = department;
        this.queuePosition = queuePosition;
        this.status = TokenStatus.GENERATED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public LocalDate getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(LocalDate tokenDate) {
        this.tokenDate = tokenDate;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getCounterRoom() {
        return counterRoom;
    }

    public void setCounterRoom(String counterRoom) {
        this.counterRoom = counterRoom;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
