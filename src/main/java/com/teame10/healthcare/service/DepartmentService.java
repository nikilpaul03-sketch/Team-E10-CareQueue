package com.teame10.healthcare.service;

import com.teame10.healthcare.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(Department department);

    Department getDepartmentById(Long id);

    Department getDepartmentByCode(String code);

    List<Department> getAllDepartments();

    List<Department> getActiveDepartments();

    Department updateDepartment(Long id, Department departmentDetails);

    void deactivateDepartment(Long id);
}
