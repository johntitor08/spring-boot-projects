package com.example.controller;

import java.util.List;

import com.example.dto.DtoEmployee;

public interface IEmployeeController {
	
	public List<DtoEmployee> findAllEmployees();

}
