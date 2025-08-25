package com.example.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoStudentIU {
	
	@NotEmpty
	@Size(min = 3, max = 10)
	private String firstName;
	
	@NotEmpty
	@Size(min = 3, max = 20)
	private String lastName;
	
	@NotEmpty
	@Email
	private String email;
	
	private Date birthOfDate;

}
