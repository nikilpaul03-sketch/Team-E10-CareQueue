package com.teame10.healthcare.repository;

import com.teame10.healthcare.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserId(Long userId);

    List<Doctor> findByDepartmentId(Long departmentId);

    List<Doctor> findByDepartmentIdAndAvailableTrue(Long departmentId);

    List<Doctor> findByAvailableTrue();

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user JOIN FETCH d.department WHERE d.available = true")
    List<Doctor> findAllWithDetails();

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user JOIN FETCH d.department WHERE d.id = :id")
    Optional<Doctor> findByIdWithDetails(@Param("id") Long id);

    Page<Doctor> findBySpecializationContainingIgnoreCase(String keyword, Pageable pageable);
}
