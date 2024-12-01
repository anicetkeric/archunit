package com.springboot.testing.archunit.service;

import com.springboot.testing.archunit.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAll();

    Employee save(Employee employee);

    Optional<Employee> getById(long id);

    void deleteById(long id);
}