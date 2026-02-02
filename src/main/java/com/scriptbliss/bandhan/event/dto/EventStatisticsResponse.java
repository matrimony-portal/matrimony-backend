package com.scriptbliss.bandhan.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Organizer dashboard stats: event counts (total, active, completed, cancelled),
 * registration counts (total, pending, paid), and totalParticipants (attended=true).
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
