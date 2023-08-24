package com.afs.restapi.service;

import com.afs.restapi.entity.Company;
import com.afs.restapi.repository.CompanyJPARepository;
import com.afs.restapi.repository.EmployeeJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void should_return_all_companies_when_get_companies_given_companies_service() {
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
}