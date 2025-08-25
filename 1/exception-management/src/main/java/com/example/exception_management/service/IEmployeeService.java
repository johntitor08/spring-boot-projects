package com.example.exception_management.service;

import com.example.exception_management.dto.DtoEmployee;

public interface IEmployeeService {
	
	public DtoEmployee findEmployeeById(Long id);

}
