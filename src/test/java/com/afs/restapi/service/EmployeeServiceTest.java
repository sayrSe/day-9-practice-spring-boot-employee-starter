package com.afs.restapi.service;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.EmployeeCreateException;
import com.afs.restapi.repository.EmployeeJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeJPARepository mockedEmployeeJPARepository;

    @BeforeEach
    void setUp() {
        mockedEmployeeJPARepository = mock(EmployeeJPARepository.class);
        employeeService = new EmployeeService(mockedEmployeeJPARepository);
    }

    @Test
    void should_return_all_employees_when_findAll_given_employee_jpa_service() {
        // Given
        Employee employee = new Employee(1L, "Lucy", 20, "Female", 3000);
        List<Employee> employees = List.of(employee);
        when(mockedEmployeeJPARepository.findAll()).thenReturn(employees);

        // When
        List<Employee> allEmployees = employeeService.findAll();

        // Then
        assertEquals(allEmployees.get(0).getId(), employee.getId());
        assertEquals(allEmployees.get(0).getName(), employee.getName());
        assertEquals(allEmployees.get(0).getAge(), employee.getAge());
        assertEquals(allEmployees.get(0).getGender(), employee.getGender());
        assertEquals(allEmployees.get(0).getSalary(), employee.getSalary());
    }

    @Test
    void should_return_the_employee_when_findById_given_employee_jpa_service_and_an_employee_id() {
        // Given
        Employee employee = new Employee(1L, "Lucy", 20, "Female", 3000);
        when(mockedEmployeeJPARepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        // When
        Employee foundEmployee = employeeService.findById(employee.getId());

        // Then
        assertEquals(employee.getId(), foundEmployee.getId());
        assertEquals(employee.getName(), foundEmployee.getName());
        assertEquals(employee.getAge(), foundEmployee.getAge());
        assertEquals(employee.getGender(), foundEmployee.getGender());
        assertEquals(employee.getSalary(), foundEmployee.getSalary());
    }

    @Test
    void should_return_employees_by_given_gender_when_findAllByGender_given_employee_jpa_service() {
        // Given
        Employee alice = new Employee(null, "Alice", 24, "Female", 9000);
        List<Employee> employees = List.of(alice);
        when(mockedEmployeeJPARepository.findAllByGender(anyString())).thenReturn(employees);

        // When
        List<Employee> foundEmployees = employeeService.findAllByGender("Female");

        // Then
        assertEquals(employees.size(), foundEmployees.size());
        assertEquals(alice.getId(), foundEmployees.get(0).getId());
        assertEquals(alice.getName(), foundEmployees.get(0).getName());
        assertEquals(alice.getAge(), foundEmployees.get(0).getAge());
        assertEquals(alice.getGender(), foundEmployees.get(0).getGender());
        assertEquals(alice.getSalary(), foundEmployees.get(0).getSalary());
    }

    @Test
    void should_return_created_employee_when_create_given_employee_jpa_service_and_employee_with_valid_age() {
        // Given
        Employee employee = new Employee(null, "Lucy", 20, "Female", 3000);
        Employee savedEmployee = new Employee(1L, "Lucy", 20, "Female", 3000);
        when(mockedEmployeeJPARepository.save(employee)).thenReturn(savedEmployee);

        // When
        Employee employeeResponse = employeeService.create(employee);

        // Then
        assertEquals(savedEmployee.getId(), employeeResponse.getId());
        assertEquals("Lucy", employeeResponse.getName());
        assertEquals(20, employeeResponse.getAge());
        assertEquals("Female", employeeResponse.getGender());
        assertEquals(3000, employeeResponse.getSalary());
    }

    @Test
    void should_throw_exception_when_create_given_employee_jpa_service_and_employee_whose_age_is_less_than_18() {
        // Given
        Employee employee = new Employee(null, "Lucy", 17, "Female", 3000);

        // When, Then
        EmployeeCreateException employeeCreateException = assertThrows(EmployeeCreateException.class, () ->
                employeeService.create(employee));
        assertEquals("Employee must be 18~65 years old", employeeCreateException.getMessage());
    }

    @Test
    void should_throw_exception_when_create_given_employee_jpa_service_and_employee_whose_age_is_greater_than_65() {
        // Given
        Employee employee = new Employee(null, "Lucy", 70, "Female", 3000);

        // When, Then
        EmployeeCreateException employeeCreateException = assertThrows(EmployeeCreateException.class, () ->
                employeeService.create(employee));
        assertEquals("Employee must be 18~65 years old", employeeCreateException.getMessage());
    }

    @Test
    void should_delete_one_time_when_delete_given_employee_jpa_service_and_active_employee() {
        // Given
        Employee employee = new Employee(null, "Lucy", 20, "Female", 3000);

        // When
        employeeService.delete(employee.getId());

        // Then
        verify(mockedEmployeeJPARepository, times(1)).deleteById(employee.getId());
    }

    @Test
    void should_return_updated_employee_when_update_given_employee_jpa_service_employee_age_and_salary() {
        // Given
        Employee employee = new Employee(1L, "Lucy", 20, "Female", 3000);
        Employee updatedEmployeeInfo = new Employee(null, null, 30, null, 10000);
        when(mockedEmployeeJPARepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        // When
        employeeService.update(employee.getId(), updatedEmployeeInfo);

        // Then
        verify(mockedEmployeeJPARepository).save(argThat(tempEmployee -> {
            assertEquals("Lucy", tempEmployee.getName());
            assertEquals("Female", tempEmployee.getGender());
            assertEquals(30, tempEmployee.getAge());
            assertEquals(10000, tempEmployee.getSalary());
            return true;
        }));
    }

    @Test
    void should_paged_employees_when_get_employees_by_page_given_employee_jpa_service_and_pageNumber_and_pageSize() {
        // Given
        int pageNumber = 2;
        int pageSize = 1;
        Employee alice = new Employee(null, "Alice", 24, "Female", 9000);
        Page<Employee> secondEmployee = new PageImpl<>(List.of(alice));
        when(mockedEmployeeJPARepository.findAll(PageRequest.of(pageNumber - 1, pageSize))).thenReturn(secondEmployee);

        // When
        List<Employee> pagedEmployees = employeeService.findByPage(pageNumber, pageSize);

        // Then
        assertEquals(pagedEmployees.size(), pageSize);
        assertEquals(pagedEmployees.get(0).getId(), alice.getId());
        assertEquals(pagedEmployees.get(0).getName(), alice.getName());
        assertEquals(pagedEmployees.get(0).getAge(), alice.getAge());
        assertEquals(pagedEmployees.get(0).getGender(), alice.getGender());
        assertEquals(pagedEmployees.get(0).getSalary(), alice.getSalary());
    }
}