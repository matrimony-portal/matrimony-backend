package com.scriptbliss.bandhan.event.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for event registration response
 * Contains registration details for API responses
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationResponse {
	
	private Long id;
	private Long userId;
	private String userName;
	private String userEmail;
	private Long eventId;
	private String eventTitle;
	private LocalDateTime registrationDate;
	private String paymentStatus;
	private Boolean attended;
	private String notes;
}
