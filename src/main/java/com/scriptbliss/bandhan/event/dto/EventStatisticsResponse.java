package com.scriptbliss.bandhan.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for event statistics response
 * Contains statistics for organizer dashboard
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStatisticsResponse {
	
	private Long totalEvents;
	private Long activeEvents;
	private Long completedEvents;
	private Long cancelledEvents;
	private Long totalRegistrations;
	private Long pendingRegistrations;
	private Long paidRegistrations;
	private Long totalParticipants;
}
