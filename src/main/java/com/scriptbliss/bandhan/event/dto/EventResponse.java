package com.scriptbliss.bandhan.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Event in API responses. Includes organizerId/organizerName and currentParticipants
 * (count of PAID registrations); status and eventType as strings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
	
	private Long id;
	private Long organizerId;
	private String organizerName;
	private String title;
	private String description;
	private LocalDateTime eventDate;
	private String venue;
	private String city;
	private String state;
	private String eventType;
	private String imageUrl;
	private Integer maxParticipants;
	private Integer currentParticipants;
	private BigDecimal registrationFee;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
