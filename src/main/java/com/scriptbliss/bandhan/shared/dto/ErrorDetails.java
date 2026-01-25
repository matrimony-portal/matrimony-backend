package com.scriptbliss.bandhan.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
	private String code;
	private String message;
	private String details;
	private String field; // For validation errors
}