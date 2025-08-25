package com.example.exception_management.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception_management.controller.IRestEmployeeController;
import com.example.exception_management.dto.DtoEmployee;
import com.example.exception_management.model.RootEntity;
import com.example.exception_management.service.IEmployeeService;

@RestController
@RequestMapping("/rest/api/employee")
public class RestEmployeeControllerImpl extends RestBaseController implements IRestEmployeeController {

	@Autowired
	private IEmployeeService employeeService;
	
	@GetMapping("/{id}")
	@Override
	public RootEntity<DtoEmployee> findEmployeeById(@PathVariable Long id) {

		return ok(employeeService.findEmployeeById(id));
		
	}

}
