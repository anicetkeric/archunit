package com.springboot.testing.archunit.service;

import com.springboot.testing.archunit.entity.Employee;
import com.springboot.testing.archunit.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee save(Employee employee) {
        return this.employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> getById(long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public void deleteById(long id) {
        this.employeeRepository.deleteById(id);
    }



}