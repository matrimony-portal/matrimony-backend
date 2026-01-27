package com.scriptbliss.bandhan.event.service;

import java.util.List;

import com.scriptbliss.bandhan.event.dto.EventRegistrationResponse;
import com.scriptbliss.bandhan.event.dto.EventStatisticsResponse;
import com.scriptbliss.bandhan.event.dto.ParticipantProfileResponse;

/**
 * Service interface for event registration management
 * Provides methods for event managers to manage registrations
 */
public interface EventRegistrationService {
	
	/**
	 * Get all registrations for an event (organizer only)
	 * @param eventId Event ID
	 * @param organizerId Organizer user ID (for authorization)
	 * @return List of registration responses
	 */
	List<EventRegistrationResponse> getEventRegistrations(Long eventId, Long organizerId);
	
	/**
	 * Get all registrations for organizer's events
	 * @param organizerId Organizer user ID
	 * @return List of registration responses
	 */
	List<EventRegistrationResponse> getOrganizerRegistrations(Long organizerId);
	
	/**
	 * Update registration payment status (organizer only)
	 * @param registrationId Registration ID
	 * @param paymentStatus New payment status
	 * @param organizerId Organizer user ID (for authorization)
	 * @return Updated registration response
	 */
	EventRegistrationResponse updatePaymentStatus(Long registrationId, String paymentStatus, Long organizerId);
	
	/**
	 * Update registration attendance (organizer only)
	 * @param registrationId Registration ID
	 * @param attended Attendance status
	 * @param organizerId Organizer user ID (for authorization)
	 * @return Updated registration response
	 */
	EventRegistrationResponse updateAttendance(Long registrationId, Boolean attended, Long organizerId);
	
	/**
	 * Get event statistics for organizer dashboard
	 * @param organizerId Organizer user ID
	 * @return Statistics response
	 */
	EventStatisticsResponse getEventStatistics(Long organizerId);
	
	/**
	 * Register a user for an event
	 * @param eventId Event ID
	 * @param userId User ID
	 * @param notes Optional registration notes
	 * @return Registration response
	 */
	EventRegistrationResponse registerForEvent(Long eventId, Long userId, String notes);
	
	/**
	 * Unregister a user from an event
	 * @param eventId Event ID
	 * @param userId User ID
	 */
	void unregisterFromEvent(Long eventId, Long userId);
	
	/**
	 * Get user's event registrations
	 * @param userId User ID
	 * @return List of registration responses
	 */
	List<EventRegistrationResponse> getUserRegistrations(Long userId);

	/**
	 * Get participant (registrant) profile for organizer to view. Only for registrations of organizer's events.
	 * @param registrationId Registration ID
	 * @param organizerId Organizer user ID (for authorization)
	 * @return Participant profile for display
	 */
	ParticipantProfileResponse getParticipantProfile(Long registrationId, Long organizerId);
}
