package com.example.spring_rest_api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.spring_rest_api.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public List<Employee> getEmployeeList() {
		
		return employeeRepository.getEmployeeList();
		
	}
	
	public Employee getEmployeeById(String id) {
		
		return employeeRepository.getEmployeeById(id);
		
	}
	
	public List<Employee> getEmployeeWithParams(String firstName, String lastName) {
		
		return employeeRepository.getEmployeeWithParams(firstName, lastName);
		
	}
	
	public Employee saveEmployee(Employee employee) {
		
		return employeeRepository.saveEmployee(employee);
		
	}
	
	public boolean deleteEmployee(String id) {
		
		return employeeRepository.deleteEmployee(id);
		
	}
	
	public Employee updateEmployee(String id, UpdateEmployeeRequest request) {
		
		return employeeRepository.updateEmployee(id, request);
		
	}
	
}
