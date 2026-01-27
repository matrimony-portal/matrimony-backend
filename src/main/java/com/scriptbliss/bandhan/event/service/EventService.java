package com.scriptbliss.bandhan.event.service;

import java.util.List;

import com.scriptbliss.bandhan.event.dto.EventRequest;
import com.scriptbliss.bandhan.event.dto.EventResponse;

/**
 * Service interface for event management operations
 * Follows Interface Segregation Principle
 */
public interface EventService {
	
	/**
	 * Create a new event
	 * @param request Event creation request
	 * @param organizerId Organizer user ID
	 * @return Created event response
	 */
	EventResponse createEvent(EventRequest request, Long organizerId);
	
	/**
	 * Get event by ID
	 * @param eventId Event ID
	 * @return Event response
	 */
	EventResponse getEventById(Long eventId);
	
	/**
	 * Get all events
	 * @return List of event responses
	 */
	List<EventResponse> getAllEvents();
	
	/**
	 * Get events by organizer
	 * @param organizerId Organizer user ID
	 * @return List of event responses
	 */
	List<EventResponse> getEventsByOrganizer(Long organizerId);
	
	/**
	 * Update event
	 * @param eventId Event ID
	 * @param request Event update request
	 * @param organizerId Organizer user ID (for authorization)
	 * @return Updated event response
	 */
	EventResponse updateEvent(Long eventId, EventRequest request, Long organizerId);
	
	/**
	 * Delete event
	 * @param eventId Event ID
	 * @param organizerId Organizer user ID (for authorization)
	 */
	void deleteEvent(Long eventId, Long organizerId);
	
	/**
	 * Update event status
	 * @param eventId Event ID
	 * @param status New status
	 * @param organizerId Organizer user ID (for authorization)
	 * @return Updated event response
	 */
	EventResponse updateEventStatus(Long eventId, String status, Long organizerId);
	
	/**
	 * Get organizer profile
	 * @param organizerId Organizer user ID
	 * @return Organizer profile response
	 */
	com.scriptbliss.bandhan.event.dto.OrganizerProfileResponse getOrganizerProfile(Long organizerId);

	/**
	 * Update organizer profile (User + optional profile fields).
	 * @param organizerId Organizer user ID
	 * @param request Update request
	 * @return Updated organizer profile response
	 */
	com.scriptbliss.bandhan.event.dto.OrganizerProfileResponse updateOrganizerProfile(Long organizerId,
			com.scriptbliss.bandhan.event.dto.OrganizerProfileUpdateRequest request);
}
