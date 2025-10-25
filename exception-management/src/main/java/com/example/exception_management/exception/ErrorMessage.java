package com.example.exception_management.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
	
	private MessageType messageType;
	private String errorCode;
	
	public String prepareErrorMessage() {
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(messageType.getMessage());
		
		if (errorCode != null) {
			
			stringBuilder.append(" Error Code: " + errorCode);
			
		}
		
		return stringBuilder.toString();
		
	}

}
