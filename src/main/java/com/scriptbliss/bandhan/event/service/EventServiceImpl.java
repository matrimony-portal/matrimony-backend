package com.scriptbliss.bandhan.event.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.UserRole;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.event.dto.EventRequest;
import com.scriptbliss.bandhan.event.dto.EventResponse;
import com.scriptbliss.bandhan.event.dto.OrganizerProfileResponse;
import com.scriptbliss.bandhan.event.dto.OrganizerProfileUpdateRequest;
import com.scriptbliss.bandhan.event.entity.Event;
import com.scriptbliss.bandhan.event.entity.Event.EventStatus;
import com.scriptbliss.bandhan.event.entity.EventRegistration;
import com.scriptbliss.bandhan.event.repository.EventRegistrationRepository;
import com.scriptbliss.bandhan.event.repository.EventRepository;
import com.scriptbliss.bandhan.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for event management
 * Follows Single Responsibility Principle and Dependency Inversion Principle
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final EventRegistrationRepository registrationRepository;
	private final ModelMapper modelMapper;
	private final EntityManager entityManager;

	@Override
	public EventResponse createEvent(EventRequest request, Long organizerId) {
		log.info("Creating event: {} by organizer: {}", request.getTitle(), organizerId);
		
		User organizer = userRepository.findById(organizerId)
				.orElseThrow(() -> new RuntimeException("Organizer not found with ID: " + organizerId));
		
		// Check if user has permission to create events
		if (!organizer.getRole().equals(UserRole.EVENT_ORGANIZER) && 
			!organizer.getRole().equals(UserRole.ADMIN)) {
			throw new RuntimeException("User does not have permission to create events");
		}
		
		Event event = modelMapper.map(request, Event.class);
		event.setOrganizer(organizer);
		event.setStatus(EventStatus.UPCOMING);
		if (request.getEventType() != null) event.setEventType(request.getEventType());
		if (request.getImageUrl() != null) event.setImageUrl(request.getImageUrl());
		
		event = eventRepository.save(event);
		log.info("Event created successfully with ID: {}", event.getId());
		
		return mapToResponse(event);
	}

	@Override
	@Transactional(readOnly = true)
	public EventResponse getEventById(Long eventId) {
		log.debug("Fetching event with ID: {}", eventId);
		
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
		
		return mapToResponse(event);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventResponse> getAllEvents() {
		log.debug("Fetching all events");
		return eventRepository.findAll().stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventResponse> getEventsByOrganizer(Long organizerId) {
		log.debug("Fetching events for organizer: {}", organizerId);
		return eventRepository.findByOrganizerId(organizerId).stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public EventResponse updateEvent(Long eventId, EventRequest request, Long organizerId) {
		log.info("Updating event: {} by organizer: {}", eventId, organizerId);
		
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
		
		// Check authorization
		if (!event.getOrganizer().getId().equals(organizerId)) {
			User organizer = userRepository.findById(organizerId)
					.orElseThrow(() -> new RuntimeException("Organizer not found"));
			if (!organizer.getRole().equals(UserRole.ADMIN)) {
				throw new RuntimeException("User does not have permission to update this event");
			}
		}
		
		// Update event fields
		event.setTitle(request.getTitle());
		event.setDescription(request.getDescription());
		event.setEventDate(request.getEventDate());
		event.setVenue(request.getVenue());
		event.setCity(request.getCity());
		event.setState(request.getState());
		if (request.getEventType() != null) event.setEventType(request.getEventType());
		if (request.getImageUrl() != null) event.setImageUrl(request.getImageUrl());
		event.setMaxParticipants(request.getMaxParticipants());
		event.setRegistrationFee(request.getRegistrationFee());
		
		event = eventRepository.save(event);
		log.info("Event updated successfully: {}", eventId);
		
		return mapToResponse(event);
	}

	@Override
	public void deleteEvent(Long eventId, Long organizerId) {
		log.info("Cancelling event: {} by organizer: {}", eventId, organizerId);
		
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
		
		// Check authorization
		if (!event.getOrganizer().getId().equals(organizerId)) {
			User organizer = userRepository.findById(organizerId)
					.orElseThrow(() -> new RuntimeException("Organizer not found"));
			if (!organizer.getRole().equals(UserRole.ADMIN)) {
				throw new RuntimeException("User does not have permission to cancel this event");
			}
		}
		
		String eventTitle = event.getTitle();
		// Notify participants with PAID status that the event was cancelled
		List<EventRegistration> registrations = registrationRepository.findByEventId(eventId);
		for (EventRegistration reg : registrations) {
			if (reg.getPaymentStatus() == EventRegistration.PaymentStatus.PAID) {
				Long userId = reg.getUser().getId();
				Query insert = entityManager.createNativeQuery(
					"INSERT INTO notifications (user_id, notification_type, title, message, is_read) VALUES (:userId, 'EVENT_CANCELLED', 'Event Cancelled', :msg, 0)");
				insert.setParameter("userId", userId);
				insert.setParameter("msg", "The event \"" + eventTitle + "\" has been cancelled. Please contact the organizer if you have questions.");
				insert.executeUpdate();
				log.debug("Notification sent to user {} for cancelled event: {}", userId, eventTitle);
			}
		}
		event.setStatus(EventStatus.CANCELLED);
		eventRepository.save(event);
		log.info("Event cancelled successfully: {}", eventId);
	}

	@Override
	public EventResponse updateEventStatus(Long eventId, String status, Long organizerId) {
		log.info("Updating event status: {} to {} by organizer: {}", eventId, status, organizerId);
		
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
		
		// Check authorization
		if (!event.getOrganizer().getId().equals(organizerId)) {
			User organizer = userRepository.findById(organizerId)
					.orElseThrow(() -> new RuntimeException("Organizer not found"));
			if (!organizer.getRole().equals(UserRole.ADMIN)) {
				throw new RuntimeException("User does not have permission to update this event");
			}
		}
		
		try {
			event.setStatus(EventStatus.valueOf(status.toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid event status: " + status + ". Valid values: UPCOMING, ONGOING, COMPLETED, CANCELLED");
		}
		
		event = eventRepository.save(event);
		log.info("Event status updated successfully: {}", eventId);
		
		return mapToResponse(event);
	}

	/**
	 * Map Event entity to EventResponse DTO
	 * Follows Single Responsibility Principle
	 */
	private EventResponse mapToResponse(Event event) {
		EventResponse response = modelMapper.map(event, EventResponse.class);
		response.setOrganizerId(event.getOrganizer().getId());
		response.setOrganizerName(event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName());
		response.setStatus(event.getStatus().name());
		response.setEventType(event.getEventType());
		response.setImageUrl(event.getImageUrl());
		// Count only PAID registrations (reject → REFUNDED excludes them)
		long currentParticipants = registrationRepository.countByEventIdAndPaymentStatus(
				event.getId(), EventRegistration.PaymentStatus.PAID);
		response.setCurrentParticipants((int) currentParticipants);
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizerProfileResponse getOrganizerProfile(Long organizerId) {
		log.debug("Fetching organizer profile for ID: {}", organizerId);
		
		User organizer = userRepository.findById(organizerId)
				.orElseThrow(() -> new ResourceNotFoundException("Organizer not found with ID: " + organizerId));
		
		// Check if user is an organizer
		if (!organizer.getRole().equals(UserRole.EVENT_ORGANIZER) && 
			!organizer.getRole().equals(UserRole.ADMIN)) {
			throw new RuntimeException("User is not an organizer");
		}
		
		// Build profile response
		OrganizerProfileResponse.OrganizerProfileResponseBuilder builder = OrganizerProfileResponse.builder()
				.id(organizer.getId())
				.email(organizer.getEmail())
				.firstName(organizer.getFirstName())
				.lastName(organizer.getLastName())
				.phone(organizer.getPhone())
				.fullName(organizer.getFirstName() + " " + organizer.getLastName());
		
		// Query profile data from profiles table if exists
		Query profileQuery = entityManager.createNativeQuery(
			"SELECT date_of_birth, gender, religion, caste, occupation, education, city, state, country, about_me " +
			"FROM profiles WHERE user_id = :userId"
		);
		profileQuery.setParameter("userId", organizerId);
		
		@SuppressWarnings("unchecked")
		List<Object[]> profileResults = profileQuery.getResultList();
		
		if (!profileResults.isEmpty()) {
			Object[] profileData = profileResults.get(0);
			LocalDate dateOfBirth = toLocalDate(profileData[0]);

			builder.dateOfBirth(dateOfBirth)
					.age(dateOfBirth != null ? Period.between(dateOfBirth, LocalDate.now()).getYears() : null)
					.gender(profileData[1] != null ? (String) profileData[1] : null)
					.religion(profileData[2] != null ? (String) profileData[2] : null)
					.caste(profileData[3] != null ? (String) profileData[3] : null)
					.occupation(profileData[4] != null ? (String) profileData[4] : null)
					.education(profileData[5] != null ? (String) profileData[5] : null)
					.city(profileData[6] != null ? (String) profileData[6] : null)
					.state(profileData[7] != null ? (String) profileData[7] : null)
					.country(profileData[8] != null ? (String) profileData[8] : null)
					.aboutMe(profileData[9] != null ? (String) profileData[9] : null);
		}
		
		// Get event statistics
		List<Event> events = eventRepository.findByOrganizerId(organizerId);
		long totalEvents = events.size();
		long upcomingEvents = events.stream()
				.filter(e -> e.getStatus() == EventStatus.UPCOMING || e.getStatus() == EventStatus.ONGOING)
				.count();
		long completedEvents = events.stream()
				.filter(e -> e.getStatus() == EventStatus.COMPLETED)
				.count();
		
		builder.totalEvents(totalEvents)
				.upcomingEvents(upcomingEvents)
				.completedEvents(completedEvents);
		
		return builder.build();
	}

	@Override
	@Transactional
	public OrganizerProfileResponse updateOrganizerProfile(Long organizerId, OrganizerProfileUpdateRequest request) {
		log.info("Updating organizer profile for ID: {}", organizerId);
		User organizer = userRepository.findById(organizerId)
				.orElseThrow(() -> new ResourceNotFoundException("Organizer not found with ID: " + organizerId));
		if (!organizer.getRole().equals(UserRole.EVENT_ORGANIZER) && !organizer.getRole().equals(UserRole.ADMIN)) {
			throw new RuntimeException("User is not an organizer");
		}
		organizer.setFirstName(request.getFirstName());
		organizer.setLastName(request.getLastName());
		if (request.getPhone() != null) {
			organizer.setPhone(request.getPhone());
		}
		userRepository.save(organizer);

		if (request.getCity() != null || request.getState() != null || request.getAboutMe() != null) {
			Query updateProfile = entityManager.createNativeQuery(
					"UPDATE profiles SET city = COALESCE(:city, city), state = COALESCE(:state, state), about_me = COALESCE(:aboutMe, about_me) WHERE user_id = :userId");
			updateProfile.setParameter("city", request.getCity());
			updateProfile.setParameter("state", request.getState());
			updateProfile.setParameter("aboutMe", request.getAboutMe());
			updateProfile.setParameter("userId", organizerId);
			updateProfile.executeUpdate();
		}

		return getOrganizerProfile(organizerId);
	}

	/**
	 * Safely convert native query DATE result to LocalDate.
	 * MySQL JDBC can return java.sql.Date or java.time.LocalDate depending on driver/config.
	 */
	private static LocalDate toLocalDate(Object value) {
		if (value == null) return null;
		if (value instanceof java.time.LocalDate) return (java.time.LocalDate) value;
		if (value instanceof java.sql.Date) return ((java.sql.Date) value).toLocalDate();
		if (value instanceof java.util.Date) {
			return ((java.util.Date) value).toInstant()
					.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
		}
		return null;
	}
}
