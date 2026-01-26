package com.scriptbliss.bandhan.shared.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	private boolean success;
	private T data;
	private String message;
	private ErrorDetails error;
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	// Success methods
	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder().success(true).data(data).build();
	}

	public static ApiResponse<Void> success(String message) {
		return ApiResponse.<Void>builder().success(true).message(message).build();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder().success(true).message(message).data(data).build();
	}

	// Error methods
	public static <T> ApiResponse<T> error(String code, String message) {
		return ApiResponse.<T>builder().success(false).error(new ErrorDetails(code, message, null, null)).build();
	}

	public static <T> ApiResponse<T> error(String code, String message, String details) {
		return ApiResponse.<T>builder().success(false).error(new ErrorDetails(code, message, details, null)).build();
	}

	public static <T> ApiResponse<T> validationError(String message, String field) {
		return ApiResponse.<T>builder().success(false)
				.error(new ErrorDetails("VALIDATION_FAILED", message, null, field)).build();
	}
}