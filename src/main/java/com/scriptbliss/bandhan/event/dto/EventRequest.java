package com.scriptbliss.bandhan.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for event creation and update requests
 * Follows Data Transfer Object pattern with validation
 */
@Getter
@Setter
@ToString
public class EventRequest {
	
	@NotBlank(message = "Title is required")
	@Size(max = 255, message = "Title must not exceed 255 characters")
	private String title;
	
	@Size(max = 5000, message = "Description must not exceed 5000 characters")
	private String description;
	
	@NotNull(message = "Event date is required")
	@Future(message = "Event date must be in the future")
	private LocalDateTime eventDate;
	
	@NotBlank(message = "Venue is required")
	@Size(max = 255, message = "Venue must not exceed 255 characters")
	private String venue;
	
	@NotBlank(message = "City is required")
	@Size(max = 100, message = "City must not exceed 100 characters")
	private String city;
	
	@NotBlank(message = "State is required")
	@Size(max = 100, message = "State must not exceed 100 characters")
	private String state;
	
	@Size(max = 50, message = "Event type must not exceed 50 characters")
	private String eventType;
	
	@Size(max = 500, message = "Image URL must not exceed 500 characters")
	private String imageUrl;
	
	@PositiveOrZero(message = "Max participants must be positive or zero")
	private Integer maxParticipants;
	
	@PositiveOrZero(message = "Registration fee must be positive or zero")
	private BigDecimal registrationFee = BigDecimal.ZERO;
}
