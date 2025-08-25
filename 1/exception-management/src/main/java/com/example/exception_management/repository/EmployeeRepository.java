package com.example.exception_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exception_management.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
