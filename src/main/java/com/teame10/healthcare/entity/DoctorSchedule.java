package com.teame10.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Team E10 - DoctorSchedule Entity
 * Represents doctor clinic sessions, operating days, slot durations, and outpatient capacity.
 */
@Entity
@Table(name = "doctor_schedules", indexes = {
    @Index(name = "idx_schedule_doctor", columnList = "doctor_id"),
    @Index(name = "idx_schedule_day", columnList = "day_of_week")
})
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Doctor is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotNull(message = "Day of week is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 15)
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Session start time is required")
    @Column(name = "session_start_time", nullable = false)
    private LocalTime sessionStartTime;

    @NotNull(message = "Session end time is required")
    @Column(name = "session_end_time", nullable = false)
    private LocalTime sessionEndTime;

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Column(name = "slot_duration_minutes", nullable = false)
    private int slotDurationMinutes = 15;

    @Min(value = 1, message = "Slot capacity must be at least 1")
    @Column(name = "slot_capacity", nullable = false)
    private int slotCapacity = 20;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DoctorSchedule() {}

    public DoctorSchedule(Doctor doctor, DayOfWeek dayOfWeek, LocalTime sessionStartTime, LocalTime sessionEndTime, int slotDurationMinutes, int slotCapacity) {
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.sessionStartTime = sessionStartTime;
        this.sessionEndTime = sessionEndTime;
        this.slotDurationMinutes = slotDurationMinutes;
        this.slotCapacity = slotCapacity;
        this.active = true;
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

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public LocalTime getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(LocalTime sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public int getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(int slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public int getSlotCapacity() {
        return slotCapacity;
    }

    public void setSlotCapacity(int slotCapacity) {
        this.slotCapacity = slotCapacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
