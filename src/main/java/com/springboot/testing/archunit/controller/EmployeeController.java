package com.springboot.testing.archunit.controller;

import com.springboot.testing.archunit.entity.Employee;
import com.springboot.testing.archunit.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
   // private final EmployeeRepository employeeRepository;


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