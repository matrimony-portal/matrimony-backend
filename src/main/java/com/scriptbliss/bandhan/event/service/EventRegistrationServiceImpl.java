package com.scriptbliss.bandhan.event.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.UserRole;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.event.dto.EventRegistrationResponse;
import com.scriptbliss.bandhan.event.dto.EventStatisticsResponse;
import com.scriptbliss.bandhan.event.dto.ParticipantProfileResponse;
import com.scriptbliss.bandhan.event.entity.Event;
import com.scriptbliss.bandhan.event.entity.Event.EventStatus;
import com.scriptbliss.bandhan.event.entity.EventRegistration;
import com.scriptbliss.bandhan.event.entity.EventRegistration.PaymentStatus;
import com.scriptbliss.bandhan.event.repository.EventRegistrationRepository;
import com.scriptbliss.bandhan.event.repository.EventRepository;
import com.scriptbliss.bandhan.shared.exception.ResourceNotFoundException;
import com.scriptbliss.bandhan.shared.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Registrations: list (by event, by organizer), payment-status, attendance, participant profile;
 * user register/unregister and my-registrations. Stats: event and registration counts.
 * When payment set to PAID: creates EVENT_REQUEST_ACCEPTED notification. Auth: organizer or ADMIN.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventRegistrationServiceImpl implements EventRegistrationService {

	private final EventRegistrationRepository registrationRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final EntityManager entityManager;

	// --- Organizer: list registrations, payment, attendance, participant profile ---

	@Override
	@Transactional(readOnly = true)
	public List<EventRegistrationResponse> getEventRegistrations(Long eventId, Long organizerId) {
		log.debug("Fetching registrations for event: {} by organizer: {}", eventId, organizerId);

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

		if (!event.getOrganizer().getId().equals(organizerId)) {
			User organizer = userRepository.findById(organizerId)
					.orElseThrow(() -> new RuntimeException("Organizer not found"));
			if (!organizer.getRole().equals(UserRole.ADMIN)) {
				throw new UnauthorizedException("User does not have permission to view registrations for this event");
			}
		}
		
		List<EventRegistration> registrations = registrationRepository.findByEventId(eventId);
		return registrations.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventRegistrationResponse> getOrganizerRegistrations(Long organizerId) {
		log.debug("Fetching all registrations for organizer: {}", organizerId);
		
		List<EventRegistration> registrations = registrationRepository.findByEventOrganizerId(organizerId);
		return registrations.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public EventRegistrationResponse updatePaymentStatus(Long registrationId, String paymentStatus, Long organizerId) {
		log.info("Updating payment status for registration: {} to {} by organizer: {}", 
				registrationId, paymentStatus, organizerId);
		
		EventRegistration registration = registrationRepository.findById(registrationId)
				.orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));
		
		// Check authorization
		if (!registration.getEvent().getOrganizer().getId().equals(organizerId)) {
			User organizer = userRepository.findById(organizerId)
					.orElseThrow(() -> new RuntimeException("Organizer not found"));
			if (!organizer.getRole().equals(UserRole.ADMIN)) {
				throw new UnauthorizedException("User does not have permission to update this registration");
			}
		}
		
		try {
			registration.setPaymentStatus(PaymentStatus.valueOf(paymentStatus.toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid payment status: " + paymentStatus + ". Valid values: PENDING, PAID, REFUNDED");
		}
		
		registration = registrationRepository.save(registration);

		if (PaymentStatus.PAID.name().equals(paymentStatus.toUpperCase())) {
			// Notify participant: registration accepted
			String eventTitle = registration.getEvent().getTitle();
			Long participantId = registration.getUser().getId();
			var insert = entityManager.createNativeQuery(
				"INSERT INTO notifications (user_id, notification_type, title, message, is_read) VALUES (:userId, 'EVENT_REQUEST_ACCEPTED', 'Event Registration Accepted', :msg, 0)");
			insert.setParameter("userId", participantId);
			insert.setParameter("msg", "Your registration for \"" + eventTitle + "\" has been accepted. You are confirmed for the event.");
			insert.executeUpdate();
			log.info("Notification sent to user {} for accepted event registration: {}", participantId, registration.getEvent().getTitle());
		}

		log.info("Payment status updated successfully for registration: {}", registrationId);
		
		return mapToResponse(registration);
	}

	@Override
	public EventRegistrationResponse updateAttendance(Long registrationId, Boolean attended, Long organizerId) {
		log.info("Updating attendance for registration: {} to {} by organizer: {}", 
				registrationId, attended, organizerId);
		
		EventRegistration registration = registrationRepository.findById(registrationId)
				.orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));
		
		// Check authorization
		if (!registration.getEvent().getOrganizer().getId().equals(organizerId)) {
			User organizer = userRepository.findById(organizerId)
					.orElseThrow(() -> new RuntimeException("Organizer not found"));
			if (!organizer.getRole().equals(UserRole.ADMIN)) {
				throw new UnauthorizedException("User does not have permission to update this registration");
			}
		}
		
		registration.setAttended(attended);
		registration = registrationRepository.save(registration);
		log.info("Attendance updated successfully for registration: {}", registrationId);
		
		return mapToResponse(registration);
	}

	// --- Statistics ---

	@Override
	@Transactional(readOnly = true)
	public EventStatisticsResponse getEventStatistics(Long organizerId) {
		log.debug("Fetching event statistics for organizer: {}", organizerId);

		List<Event> events = eventRepository.findByOrganizerId(organizerId);
		
		long totalEvents = events.size();
		long activeEvents = events.stream()
				.filter(e -> e.getStatus() == EventStatus.UPCOMING || e.getStatus() == EventStatus.ONGOING)
				.count();
		long completedEvents = events.stream()
				.filter(e -> e.getStatus() == EventStatus.COMPLETED)
				.count();
		long cancelledEvents = events.stream()
				.filter(e -> e.getStatus() == EventStatus.CANCELLED)
				.count();
		
		List<EventRegistration> allRegistrations = registrationRepository.findByEventOrganizerId(organizerId);
		long totalRegistrations = allRegistrations.size();
		long pendingRegistrations = allRegistrations.stream()
				.filter(r -> r.getPaymentStatus() == PaymentStatus.PENDING)
				.count();
		long paidRegistrations = allRegistrations.stream()
				.filter(r -> r.getPaymentStatus() == PaymentStatus.PAID)
				.count();
		long totalParticipants = allRegistrations.stream()
				.filter(r -> r.getAttended() != null && r.getAttended())
				.count();
		
		EventStatisticsResponse stats = new EventStatisticsResponse();
		stats.setTotalEvents(totalEvents);
		stats.setActiveEvents(activeEvents);
		stats.setCompletedEvents(completedEvents);
		stats.setCancelledEvents(cancelledEvents);
		stats.setTotalRegistrations(totalRegistrations);
		stats.setPendingRegistrations(pendingRegistrations);
		stats.setPaidRegistrations(paidRegistrations);
		stats.setTotalParticipants(totalParticipants);
		
		return stats;
	}

	// --- User: register, unregister, my-registrations ---

	@Override
	public EventRegistrationResponse registerForEvent(Long eventId, Long userId, String notes) {
		log.info("Registering user: {} for event: {}", userId, eventId);

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

		Optional<EventRegistration> existingRegistration = registrationRepository.findByUserIdAndEventId(userId, eventId);
		if (existingRegistration.isPresent()) {
			throw new RuntimeException("User is already registered for this event");
		}

		long currentRegistrations = registrationRepository.countByEventId(eventId);
		if (event.getMaxParticipants() != null && currentRegistrations >= event.getMaxParticipants()) {
			throw new RuntimeException("Event is full. Maximum participants reached.");
		}

		if (event.getStatus() != Event.EventStatus.UPCOMING) {
			throw new RuntimeException("Event is not open for registration. Status: " + event.getStatus());
		}
		
		EventRegistration registration = new EventRegistration();
		registration.setUser(user);
		registration.setEvent(event);
		registration.setNotes(notes);
		registration.setPaymentStatus(EventRegistration.PaymentStatus.PENDING);
		registration.setAttended(false);
		registration.setRegistrationDate(LocalDateTime.now());
		
		registration = registrationRepository.save(registration);
		log.info("User registered successfully for event: {}", eventId);
		
		return mapToResponse(registration);
	}

	@Override
	public void unregisterFromEvent(Long eventId, Long userId) {
		log.info("Unregistering user: {} from event: {}", userId, eventId);
		
		EventRegistration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Registration not found for user: " + userId + " and event: " + eventId));
		
		registrationRepository.delete(registration);
		log.info("User unregistered successfully from event: {}", eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventRegistrationResponse> getUserRegistrations(Long userId) {
		log.debug("Fetching registrations for user: {}", userId);
		
		List<EventRegistration> registrations = registrationRepository.findByUserId(userId);
		return registrations.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public ParticipantProfileResponse getParticipantProfile(Long registrationId, Long organizerId) {
		log.debug("Fetching participant profile for registration: {} by organizer: {}", registrationId, organizerId);
		// Must be organizer of the event or ADMIN
		EventRegistration registration = registrationRepository.findById(registrationId)
				.orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));
		if (!registration.getEvent().getOrganizer().getId().equals(organizerId)) {
			User organizer = userRepository.findById(organizerId)
					.orElseThrow(() -> new RuntimeException("Organizer not found"));
			if (!organizer.getRole().equals(UserRole.ADMIN)) {
				throw new UnauthorizedException("User does not have permission to view this participant");
			}
		}
		User u = registration.getUser();
		ParticipantProfileResponse r = new ParticipantProfileResponse();
		r.setUserId(u.getId());
		r.setUserName(u.getFirstName() + " " + u.getLastName());
		r.setUserEmail(u.getEmail());

		Query q = entityManager.createNativeQuery(
			"SELECT date_of_birth, gender, religion, caste, occupation, education, city, state, country, about_me FROM profiles WHERE user_id = :userId");
		q.setParameter("userId", u.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> rows = q.getResultList();
		if (!rows.isEmpty()) {
			Object[] row = rows.get(0);
			LocalDate dob = toLocalDate(row[0]);
			r.setAge(dob != null ? Period.between(dob, LocalDate.now()).getYears() : null);
			r.setGender(row[1] != null ? (String) row[1] : null);
			r.setReligion(row[2] != null ? (String) row[2] : null);
			r.setCaste(row[3] != null ? (String) row[3] : null);
			r.setOccupation(row[4] != null ? (String) row[4] : null);
			r.setEducation(row[5] != null ? (String) row[5] : null);
			r.setCity(row[6] != null ? (String) row[6] : null);
			r.setState(row[7] != null ? (String) row[7] : null);
			r.setCountry(row[8] != null ? (String) row[8] : null);
			r.setAboutMe(row[9] != null ? (String) row[9] : null);
		}
		return r;
	}

	/** Normalize native query DATE to LocalDate (JDBC may return java.sql.Date or LocalDate). */
	private static LocalDate toLocalDate(Object value) {
		if (value == null) return null;
		if (value instanceof java.time.LocalDate) return (java.time.LocalDate) value;
		if (value instanceof java.sql.Date) return ((java.sql.Date) value).toLocalDate();
		if (value instanceof java.util.Date) {
			return ((java.util.Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
		}
		return null;
	}

	private EventRegistrationResponse mapToResponse(EventRegistration registration) {
		EventRegistrationResponse response = new EventRegistrationResponse();
		response.setId(registration.getId());
		response.setUserId(registration.getUser().getId());
		response.setUserName(registration.getUser().getFirstName() + " " + registration.getUser().getLastName());
		response.setUserEmail(registration.getUser().getEmail());
		response.setEventId(registration.getEvent().getId());
		response.setEventTitle(registration.getEvent().getTitle());
		response.setEventDate(registration.getEvent().getEventDate());
		response.setVenue(registration.getEvent().getVenue());
		response.setRegistrationDate(registration.getRegistrationDate());
		response.setPaymentStatus(registration.getPaymentStatus().name());
		response.setAttended(registration.getAttended());
		response.setNotes(registration.getNotes());
		return response;
	}
}
