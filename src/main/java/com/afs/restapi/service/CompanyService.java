package com.afs.restapi.service;

import com.afs.restapi.entity.Company;
import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.CompanyNotFoundException;
import com.afs.restapi.repository.CompanyJPARepository;
import com.afs.restapi.repository.EmployeeJPARepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyJPARepository companyJPARepository;
    private final EmployeeJPARepository employeeJPARepository;

    public CompanyService(CompanyJPARepository companyJPARepository, EmployeeJPARepository employeeJPARepository) {
        this.companyJPARepository = companyJPARepository;
        this.employeeJPARepository = employeeJPARepository;
    }

    public List<Company> findAll() {
        return companyJPARepository.findAll();
    }

    public List<Company> findByPage(Integer pageNumber, Integer pageSize) {
        return companyJPARepository.findAll(PageRequest.of(pageNumber - 1, pageSize)).toList();
    }

    public Company findById(Long id) {
        return companyJPARepository.findById(id).orElseThrow(CompanyNotFoundException::new);
    }

    public void update(Long id, Company company) {
        Company toBeUpdatedCompany = findById(id);
        toBeUpdatedCompany.setName(company.getName());
        companyJPARepository.save(toBeUpdatedCompany);
    }

    public Company create(Company company) {
        return companyJPARepository.save(company);
    }

    public List<Employee> findEmployeesByCompanyId(Long id) {
        return employeeJPARepository.findByCompanyId(id);
    }

    public void delete(Long id) {
        companyJPARepository.deleteById(id);
    }
}
