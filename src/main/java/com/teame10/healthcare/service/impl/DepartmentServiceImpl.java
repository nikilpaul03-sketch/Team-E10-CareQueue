package com.teame10.healthcare.service.impl;

import com.teame10.healthcare.entity.Department;
import com.teame10.healthcare.exception.ResourceNotFoundException;
import com.teame10.healthcare.repository.DepartmentRepository;
import com.teame10.healthcare.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByCode(department.getCode())) {
            throw new IllegalArgumentException("Department code already exists: " + department.getCode());
        }
        if (departmentRepository.existsByName(department.getName())) {
            throw new IllegalArgumentException("Department name already exists: " + department.getName());
        }
        return departmentRepository.save(department);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Override
    public Department getDepartmentByCode(String code) {
        return departmentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with code: " + code));
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public List<Department> getActiveDepartments() {
        return departmentRepository.findByActiveTrue();
    }

    @Override
    @Transactional
    public Department updateDepartment(Long id, Department details) {
        Department existing = getDepartmentById(id);
        existing.setName(details.getName());
        existing.setDescription(details.getDescription());
        existing.setLocation(details.getLocation());
        existing.setActive(details.isActive());
        return departmentRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivateDepartment(Long id) {
        Department department = getDepartmentById(id);
        department.setActive(false);
        departmentRepository.save(department);
    }
}
