package com.scriptbliss.bandhan.event.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.event.dto.EventRegistrationResponse;
import com.scriptbliss.bandhan.event.dto.EventRequest;
import com.scriptbliss.bandhan.event.dto.EventResponse;
import com.scriptbliss.bandhan.event.dto.EventStatisticsResponse;
import com.scriptbliss.bandhan.event.dto.ParticipantProfileResponse;
import com.scriptbliss.bandhan.event.service.EventRegistrationService;
import com.scriptbliss.bandhan.event.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for event management endpoints
 * Follows RESTful API design principles
 */
@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Event Management", description = "APIs for managing matrimonial events")
public class EventController {

	private final EventService eventService;
	private final EventRegistrationService registrationService;

	@PostMapping
	@Operation(summary = "Create Event", description = "Create a new event (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<EventResponse> createEvent(
			@RequestBody @Valid EventRequest request,
			@RequestParam Long organizerId) {
		log.info("Create event request received");
		EventResponse response = eventService.createEvent(request, organizerId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@Operation(summary = "Get All Events", description = "Retrieve all events (public endpoint)")
	public ResponseEntity<List<EventResponse>> getAllEvents() {
		log.debug("Get all events request received");
		List<EventResponse> events = eventService.getAllEvents();
		return ResponseEntity.ok(events);
	}

	@GetMapping("/{eventId}")
	@Operation(summary = "Get Event by ID", description = "Retrieve event details by ID (public endpoint)")
	public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId) {
		log.debug("Get event by ID request received: {}", eventId);
		EventResponse event = eventService.getEventById(eventId);
		return ResponseEntity.ok(event);
	}

	@GetMapping("/organizer/{organizerId}")
	@Operation(summary = "Get Events by Organizer", description = "Retrieve all events organized by a specific organizer")
	public ResponseEntity<List<EventResponse>> getEventsByOrganizer(@PathVariable Long organizerId) {
		log.debug("Get events by organizer request received: {}", organizerId);
		List<EventResponse> events = eventService.getEventsByOrganizer(organizerId);
		return ResponseEntity.ok(events);
	}

	@GetMapping("/my-events")
	@Operation(summary = "Get My Events", description = "Retrieve all events organized by the current user")
	public ResponseEntity<List<EventResponse>> getMyEvents(@RequestParam Long organizerId) {
		log.debug("Get my events request received");
		List<EventResponse> events = eventService.getEventsByOrganizer(organizerId);
		return ResponseEntity.ok(events);
	}

	@PutMapping("/{eventId}")
	@Operation(summary = "Update Event", description = "Update event details (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<EventResponse> updateEvent(
			@PathVariable Long eventId,
			@RequestBody @Valid EventRequest request,
			@RequestParam Long organizerId) {
		log.info("Update event request received: {}", eventId);
		EventResponse response = eventService.updateEvent(eventId, request, organizerId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{eventId}/status")
	@Operation(summary = "Update Event Status", description = "Update event status (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<EventResponse> updateEventStatus(
			@PathVariable Long eventId,
			@RequestParam String status,
			@RequestParam Long organizerId) {
		log.info("Update event status request received: {} to {}", eventId, status);
		EventResponse response = eventService.updateEventStatus(eventId, status, organizerId);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{eventId}")
	@Operation(summary = "Delete Event", description = "Delete an event (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<Void> deleteEvent(
			@PathVariable Long eventId,
			@RequestParam Long organizerId) {
		log.info("Delete event request received: {}", eventId);
		eventService.deleteEvent(eventId, organizerId);
		return ResponseEntity.noContent().build();
	}

	// ===========================================
	// Event Registration Management APIs
	// ===========================================

	@GetMapping("/{eventId}/registrations")
	@Operation(summary = "Get Event Registrations", description = "Get all registrations for an event (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<List<EventRegistrationResponse>> getEventRegistrations(
			@PathVariable Long eventId,
			@RequestParam Long organizerId) {
		log.debug("Get registrations for event: {}", eventId);
		List<EventRegistrationResponse> registrations = registrationService.getEventRegistrations(eventId, organizerId);
		return ResponseEntity.ok(registrations);
	}

	@GetMapping("/registrations/my-events")
	@Operation(summary = "Get All My Event Registrations", description = "Get all registrations for organizer's events (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<List<EventRegistrationResponse>> getMyEventRegistrations(@RequestParam Long organizerId) {
		log.debug("Get all registrations for organizer's events");
		List<EventRegistrationResponse> registrations = registrationService.getOrganizerRegistrations(organizerId);
		return ResponseEntity.ok(registrations);
	}

	@PutMapping("/registrations/{registrationId}/payment-status")
	@Operation(summary = "Update Registration Payment Status", description = "Update payment status of a registration (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<EventRegistrationResponse> updatePaymentStatus(
			@PathVariable Long registrationId,
			@RequestParam String paymentStatus,
			@RequestParam Long organizerId) {
		log.info("Update payment status for registration: {} to {}", registrationId, paymentStatus);
		EventRegistrationResponse response = registrationService.updatePaymentStatus(registrationId, paymentStatus, organizerId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/registrations/{registrationId}/participant-profile")
	@Operation(summary = "Get Participant Profile", description = "View profile of a user who sent a registration request (organizer only)")
	public ResponseEntity<ParticipantProfileResponse> getParticipantProfile(
			@PathVariable Long registrationId,
			@RequestParam Long organizerId) {
		log.debug("Get participant profile for registration: {}", registrationId);
		ParticipantProfileResponse response = registrationService.getParticipantProfile(registrationId, organizerId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/registrations/{registrationId}/attendance")
	@Operation(summary = "Update Registration Attendance", description = "Update attendance status of a registration (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<EventRegistrationResponse> updateAttendance(
			@PathVariable Long registrationId,
			@RequestParam Boolean attended,
			@RequestParam Long organizerId) {
		log.info("Update attendance for registration: {} to {}", registrationId, attended);
		EventRegistrationResponse response = registrationService.updateAttendance(registrationId, attended, organizerId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/statistics")
	@Operation(summary = "Get Event Statistics", description = "Get event statistics for organizer dashboard (requires EVENT_ORGANIZER or ADMIN role)")
	public ResponseEntity<EventStatisticsResponse> getEventStatistics(@RequestParam Long organizerId) {
		log.debug("Get event statistics request received");
		EventStatisticsResponse statistics = registrationService.getEventStatistics(organizerId);
		return ResponseEntity.ok(statistics);
	}

	// ===========================================
	// User Event Registration APIs
	// ===========================================

	@PostMapping("/{eventId}/register")
	@Operation(summary = "Register for Event", description = "Register the current user for an event")
	public ResponseEntity<EventRegistrationResponse> registerForEvent(
			@PathVariable Long eventId,
			@RequestParam Long userId,
			@RequestParam(required = false) String notes) {
		log.info("Register for event request received: {}", eventId);
		EventRegistrationResponse response = registrationService.registerForEvent(eventId, userId, notes);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{eventId}/register")
	@Operation(summary = "Unregister from Event", description = "Unregister the current user from an event")
	public ResponseEntity<Void> unregisterFromEvent(
			@PathVariable Long eventId,
			@RequestParam Long userId) {
		log.info("Unregister from event request received: {}", eventId);
		registrationService.unregisterFromEvent(eventId, userId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/my-registrations")
	@Operation(summary = "Get My Event Registrations", description = "Get all events the current user has registered for")
	public ResponseEntity<List<EventRegistrationResponse>> getMyRegistrations(@RequestParam Long userId) {
		log.debug("Get my registrations request received");
		List<EventRegistrationResponse> registrations = registrationService.getUserRegistrations(userId);
		return ResponseEntity.ok(registrations);
	}
}
