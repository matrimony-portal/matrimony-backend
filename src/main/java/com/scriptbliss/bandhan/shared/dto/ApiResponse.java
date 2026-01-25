package com.scriptbliss.bandhan.shared.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private T data;
	private String message;
	private ErrorDetails error;
	private LocalDateTime timestamp = LocalDateTime.now();

	// Success constructors
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null, null, LocalDateTime.now());
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(true, data, message, null, LocalDateTime.now());
	}

	public static ApiResponse<Void> success(String message) {
		return new ApiResponse<>(true, null, message, null, LocalDateTime.now());
	}

	// Error constructors
	public static <T> ApiResponse<T> error(String code, String message) {
		return new ApiResponse<>(false, null, null, new ErrorDetails(code, message, null, null), LocalDateTime.now());
	}

	public static <T> ApiResponse<T> error(String code, String message, String details) {
		return new ApiResponse<>(false, null, null, new ErrorDetails(code, message, details, null),
				LocalDateTime.now());
	}

	public static <T> ApiResponse<T> validationError(String message, String field) {
		return new ApiResponse<>(false, null, null, new ErrorDetails("VALIDATION_FAILED", message, null, field),
				LocalDateTime.now());
	}
}