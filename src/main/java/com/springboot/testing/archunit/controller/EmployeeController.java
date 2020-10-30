package com.springboot.testing.archunit.controller;

import com.springboot.testing.archunit.domain.Employee;
import com.springboot.testing.archunit.repository.EmployeeRepository;
import com.springboot.testing.archunit.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @GetMapping()
    public List<Employee> showNewEmployeeForm() {
        return employeeService.getAll();

    }

    @PostMapping()
    public Employee save(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable(value = "id") long id) {
        return employeeService.getById(id).orElseThrow(() -> new RuntimeException("not found"));
    }

}