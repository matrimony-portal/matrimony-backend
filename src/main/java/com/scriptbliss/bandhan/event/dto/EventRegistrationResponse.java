package com.scriptbliss.bandhan.event.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registration in API responses. Includes user and event summary, paymentStatus, attended, notes.
 * eventDate and venue support "My Registered Events" views without extra lookups.
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
	private LocalDateTime eventDate;
	private String venue;
	private LocalDateTime registrationDate;
	private String paymentStatus;
	private Boolean attended;
	private String notes;
}
