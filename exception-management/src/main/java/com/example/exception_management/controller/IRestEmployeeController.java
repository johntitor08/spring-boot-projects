package com.example.exception_management.controller;

import com.example.exception_management.dto.DtoEmployee;
import com.example.exception_management.model.RootEntity;

public interface IRestEmployeeController {
	
	public RootEntity<DtoEmployee> findEmployeeById(Long id);

}
