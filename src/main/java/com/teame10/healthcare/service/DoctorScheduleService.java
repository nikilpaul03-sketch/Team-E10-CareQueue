package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.DoctorSchedule;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorScheduleService {

    DoctorSchedule createSchedule(DoctorSchedule schedule);

    DoctorSchedule getScheduleById(Long id);

    List<DoctorSchedule> getActiveSchedulesByDoctor(Long doctorId);

    DoctorSchedule getScheduleForDoctorOnDay(Long doctorId, DayOfWeek dayOfWeek);

    DoctorSchedule updateSchedule(Long id, DoctorSchedule scheduleDetails);

    void deactivateSchedule(Long id);
}
