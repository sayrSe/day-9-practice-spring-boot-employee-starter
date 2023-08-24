package com.afs.restapi.service;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.EmployeeCreateException;
import com.afs.restapi.exception.EmployeeNotFoundException;
import com.afs.restapi.repository.EmployeeJPARepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeJPARepository employeeJPARepository;

    public EmployeeService(EmployeeJPARepository employeeJPARepository) {
        this.employeeJPARepository = employeeJPARepository;
    }

    public List<Employee> findAll() {
        return employeeJPARepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeJPARepository.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);
    }

    public void update(Long id, Employee employee) {
        Employee toBeUpdatedEmployee = findById(id);
        if (employee.getSalary() != null) {
            toBeUpdatedEmployee.setSalary(employee.getSalary());
        }
        if (employee.getAge() != null) {
            toBeUpdatedEmployee.setAge(employee.getAge());
        }
        employeeJPARepository.save(toBeUpdatedEmployee);
    }

    public List<Employee> findAllByGender(String gender) {
        return employeeJPARepository.findAllByGender(gender);
    }

    public Employee create(Employee employee) {
        if (employee.hasInvalidAge()) {
            throw new EmployeeCreateException();
        }
        return employeeJPARepository.save(employee);
    }

    public List<Employee> findByPage(Integer pageNumber, Integer pageSize) {
        return employeeJPARepository.findAll(PageRequest.of(pageNumber - 1, pageSize)).toList();
    }

    public void delete(Long id) {
        employeeJPARepository.deleteById(id);
    }
}
