package com.example.spring_rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_rest_api.services.Employee;
import com.example.spring_rest_api.services.EmployeeService;
import com.example.spring_rest_api.services.UpdateEmployeeRequest;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/rest/api/employee")
public class RestEmployeeController {
	
	@Autowired
	private EmployeeService employeeService;

	@GetMapping(path = "/list")
	public List<Employee> getEmployeeList() {
		
		return employeeService.getEmployeeList();
		
	}
	
	@GetMapping(path = "/list/{id}")
	public Employee getEmployeeById(@PathVariable(required = true) String id) {
		
		return employeeService.getEmployeeById(id);
		
	}
	
	@GetMapping(path = "/with-params")
	public List<Employee> getEmployeeWithParams(@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
		
		return employeeService.getEmployeeWithParams(firstName, lastName);
		
	}
	
	@PostMapping(path = "/save")
	public Employee saveEmployee(@RequestBody Employee employee) {
		
		return employeeService.saveEmployee(employee);
		
	}
	
	@DeleteMapping(path = "/delete/{id}")
	public boolean deleteEmployee(@PathVariable String id) {
		
		return employeeService.deleteEmployee(id);
		
	}
	
	@PutMapping("/update/{id}")
	public Employee updateEmployee (@PathVariable String id, @RequestBody UpdateEmployeeRequest request) {
		
		return employeeService.updateEmployee(id, request);
		
	}
	
}
