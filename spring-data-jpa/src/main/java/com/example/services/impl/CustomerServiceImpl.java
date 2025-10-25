package com.example.services.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.DtoAddress;
import com.example.dto.DtoCustomer;
import com.example.entities.Address;
import com.example.entities.Customer;
import com.example.repository.CustomerRepository;
import com.example.services.ICustomerService;

@Service
public class CustomerServiceImpl implements ICustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public DtoCustomer findCustomerById(Long id) {
		
		DtoCustomer dtoCustomer = new DtoCustomer();
		DtoAddress dtoAddress = new DtoAddress();
		Optional<Customer> optional = customerRepository.findById(id);
		
		if (optional.isEmpty()) {
			
			return null;
			
		}
		
		Customer customer = optional.get();
		Address address = optional.get().getAddress();
		BeanUtils.copyProperties(customer, dtoCustomer);
		BeanUtils.copyProperties(address, dtoAddress);
		dtoCustomer.setDtoAddress(dtoAddress);
		return dtoCustomer;
		
	}
	
}
