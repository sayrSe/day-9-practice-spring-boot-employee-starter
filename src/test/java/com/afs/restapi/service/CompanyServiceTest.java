package com.afs.restapi.service;

import com.afs.restapi.entity.Company;
import com.afs.restapi.entity.Employee;
import com.afs.restapi.repository.CompanyJPARepository;
import com.afs.restapi.repository.EmployeeJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    private CompanyService companyService;
    private CompanyJPARepository mockedCompanyJPARepository;
    private EmployeeJPARepository mockedEmployeeJPARepository;

    @BeforeEach
    void setUp() {
        mockedCompanyJPARepository = mock(CompanyJPARepository.class);
        mockedEmployeeJPARepository = mock(EmployeeJPARepository.class);
        companyService = new CompanyService(mockedCompanyJPARepository, mockedEmployeeJPARepository);
    }

    @Test
    void should_return_all_companies_when_get_companies_given_company_jpa_service() {
        // Given
        Company company = new Company(1L, "OOCL");
        List<Company> companies = List.of(company);
        when(mockedCompanyJPARepository.findAll()).thenReturn(companies);

        // When
        List<Company> allCompanies = companyService.findAll();

        // Then
        assertEquals(allCompanies.get(0).getId(), company.getId());
        assertEquals(allCompanies.get(0).getName(), company.getName());
    }

    @Test
    void should_return_the_company_when_get_company_given_company_jpa_service_and_an_company_id() {
        // Given
        Company company = new Company(1L, "OOCL");
        when(mockedCompanyJPARepository.findById(company.getId())).thenReturn(Optional.of(company));

        // When
        Company foundCompany = companyService.findById(company.getId());

        // Then
        assertEquals(company.getId(), foundCompany.getId());
        assertEquals(company.getName(), foundCompany.getName());
    }

    @Test
    void should_return_employees_by_given_company_when_get_find_employees_by_company_id_given_company_jpa_service() {
        // Given
        Company company = new Company(1L, "OOCL");
        Employee alice = new Employee(null, "Alice", 24, "Female", 9000);

        List<Employee> employees = List.of(alice);
        when(mockedEmployeeJPARepository.findByCompanyId(company.getId())).thenReturn(employees);

        // When
        List<Employee> foundEmployees = companyService.findEmployeesByCompanyId(company.getId());

        // Then
        assertEquals(foundEmployees.get(0).getId(), alice.getId());
        assertEquals(foundEmployees.get(0).getName(), alice.getName());
        assertEquals(foundEmployees.get(0).getAge(), alice.getAge());
        assertEquals(foundEmployees.get(0).getGender(), alice.getGender());
        assertEquals(foundEmployees.get(0).getSalary(), alice.getSalary());
    }

    @Test
    void should_delete_one_time_when_delete_given_company_jpa_service_and_active_company() {
        // Given
        Company company = new Company(1L, "OOCL");

        // When
        companyService.delete(company.getId());

        // Then
        verify(mockedCompanyJPARepository, times(1)).deleteById(company.getId());
    }

    @Test
    void should_return_created_company_when_create_given_company_jpa_service_and_company() {
        // Given
        Company company = new Company(1L, "OOCL");
        Company savedCompany = new Company(1L, "OOCL");
        when(mockedCompanyJPARepository.save(company)).thenReturn(savedCompany);

        // When
        Company companyResponse = companyService.create(company);

        // Then
        assertEquals(savedCompany.getId(), companyResponse.getId());
        assertEquals("OOCL", companyResponse.getName());
    }

    @Test
    void should_return_updated_company_when_update_given_company_jpa_service_and_company_name() {
        // Given
        Company company = new Company(1L, "OOCL");
        Company updatedCompanyInfo = new Company(null, "Thoughtworks");
        when(mockedCompanyJPARepository.findById(company.getId())).thenReturn(Optional.of(company));

        // When
        companyService.update(company.getId(), updatedCompanyInfo);

        // Then
        verify(mockedCompanyJPARepository).save(argThat(tempCompany -> {
            assertEquals(company.getId(), tempCompany.getId());
            assertEquals("Thoughtworks", tempCompany.getName());
            return true;
        }));
    }

    @Test
    void should_paged_companies_when_get_companies_by_page_given_company_service_and_pageNumber_and_pageSize() {
        // Given
        int pageNumber = 1;
        int pageSize = 1;
        Company company = new Company(1L, "OOCL");
        Page<Company> pagedCompany = new PageImpl<>(List.of(company));
        when(mockedCompanyJPARepository.findAll(PageRequest.of(0, pageSize))).thenReturn(pagedCompany);

        // When
        List<Company> pagedCompanies = companyService.findByPage(pageNumber, pageSize);

        // Then
        assertEquals(pagedCompanies.get(0).getId(), company.getId());
        assertEquals(pagedCompanies.get(0).getName(), company.getName());
    }
}