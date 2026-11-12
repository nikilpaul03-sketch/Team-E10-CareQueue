package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.DoctorSchedule;
import com.teame10.healthcare.exception.ResourceNotFoundException;
import com.teame10.healthcare.repository.DoctorScheduleRepository;
import com.teame10.healthcare.service.DoctorScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;

    public DoctorScheduleServiceImpl(DoctorScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    @Transactional
    public DoctorSchedule createSchedule(DoctorSchedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public DoctorSchedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
    }

    @Override
    public List<DoctorSchedule> getActiveSchedulesByDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorIdAndActiveTrue(doctorId);
    }

    @Override
    public DoctorSchedule getScheduleForDoctorOnDay(Long doctorId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(doctorId, dayOfWeek)
                .orElseThrow(() -> new ResourceNotFoundException("No active schedule found for doctor id " + doctorId + " on " + dayOfWeek));
    }

    @Override
    @Transactional
    public DoctorSchedule updateSchedule(Long id, DoctorSchedule details) {
        DoctorSchedule existing = getScheduleById(id);
        existing.setDayOfWeek(details.getDayOfWeek());
        existing.setSessionStartTime(details.getSessionStartTime());
        existing.setSessionEndTime(details.getSessionEndTime());
        existing.setSlotDurationMinutes(details.getSlotDurationMinutes());
        existing.setSlotCapacity(details.getSlotCapacity());
        existing.setActive(details.isActive());
        return scheduleRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivateSchedule(Long id) {
        DoctorSchedule schedule = getScheduleById(id);
        schedule.setActive(false);
        scheduleRepository.save(schedule);
    }
}
