package com.example.spring_rest_api.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.spring_rest_api.services.Employee;

@Configuration
public class AppConfig {
	
	@Bean
	List<Employee> employeeList() {
		
		List<Employee> employeeList = new ArrayList<>();
		employeeList.add(new Employee("1", "Enes", "Bayram"));
		employeeList.add(new Employee("2", "Yakup", "Reçber"));
		employeeList.add(new Employee("3", "Bilal", "Çamur"));
		employeeList.add(new Employee("4", "Harun", "Kaymazalp"));
		employeeList.add(new Employee("5", "Yasin", "Yazıcı"));
		return employeeList;
		
	}

}
