package com.scriptbliss.bandhan.shared.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import org.springframework.web.servlet.NoHandlerFoundException;

import com.scriptbliss.bandhan.shared.dto.ApiResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		log.warn("Validation failed: {}", errors);
		return ResponseEntity.badRequest()
				.body(ApiResponse.error("VALIDATION_FAILED", "Validation failed", errors.toString()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
		log.warn("Constraint violation: {}", ex.getMessage());
		return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_FAILED", ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmptyRequestBody(HttpMessageNotReadableException ex) {
		log.warn("Empty or invalid request body: {}", ex.getMessage());
		return ResponseEntity.badRequest()
				.body(ApiResponse.error("INVALID_REQUEST_BODY", "Request body is required and must be valid JSON"));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
		log.warn("Business exception: {} - {}", ex.getCode(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
				.body(ApiResponse.error(ex.getCode(), ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
		log.warn("Resource not found: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error("RESOURCE_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
		log.warn("Unauthorized access: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("UNAUTHORIZED", ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
		log.warn("Illegal argument: {}", ex.getMessage());
		return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_ARGUMENT", ex.getMessage()));
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException ex) {
		log.warn("Route not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error("ROUTE_NOT_FOUND", "The requested endpoint does not exist"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, WebRequest request) {
		log.error("Unexpected error occurred: ", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("SYSTEM_INTERNAL_ERROR", "An unexpected error occurred"));
	}
}