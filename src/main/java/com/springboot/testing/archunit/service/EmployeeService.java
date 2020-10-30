package com.springboot.testing.archunit.service;

import com.springboot.testing.archunit.domain.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAll();

    Employee save(Employee employee);

    Optional<Employee> getById(long id);

    void deleteById(long id);
}