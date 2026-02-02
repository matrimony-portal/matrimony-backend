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
import com.scriptbliss.bandhan.event.dto.OrganizerProfileResponse;
import com.scriptbliss.bandhan.event.dto.OrganizerProfileUpdateRequest;
import com.scriptbliss.bandhan.event.service.EventRegistrationService;
import com.scriptbliss.bandhan.event.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Organizer-scoped REST API under /bandhan/organizers/{organizerId}.
 *
 * <p>Puts organizerId in the path instead of query; same service layer as {@link EventController}.
 * Sections: event CRUD, registrations (list, payment, attendance), statistics, organizer profile (GET/PUT).
 * All operations require the user to be the organizer or ADMIN (enforced in services).
 */
@RestController
@RequestMapping("/organizers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Event Organizer Management", description = "APIs for event organizers to manage their events and registrations")
public class EventOrganizerController {

	private final EventService eventService;
	private final EventRegistrationService registrationService;

	// ===========================================
	// Event Management APIs
	// ===========================================

	@PostMapping("/{organizerId}/events")
	@Operation(summary = "Create Event", description = "Create a new event for the organizer")
	public ResponseEntity<EventResponse> createEvent(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@RequestBody @Valid EventRequest request) {
		log.info("Create event request received for organizer: {}", organizerId);
		EventResponse response = eventService.createEvent(request, organizerId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{organizerId}/events")
	@Operation(summary = "Get Organizer's Events", description = "Retrieve all events organized by the specified organizer")
	public ResponseEntity<List<EventResponse>> getOrganizerEvents(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId) {
		log.debug("Get events for organizer: {}", organizerId);
		List<EventResponse> events = eventService.getEventsByOrganizer(organizerId);
		return ResponseEntity.ok(events);
	}

	@GetMapping("/{organizerId}/events/{eventId}")
	@Operation(summary = "Get Event by ID", description = "Retrieve a specific event by ID (must belong to the organizer)")
	public ResponseEntity<EventResponse> getEventById(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@Parameter(description = "Event ID") @PathVariable Long eventId) {
		log.debug("Get event {} for organizer: {}", eventId, organizerId);
		EventResponse event = eventService.getEventById(eventId);
		// Verify event belongs to organizer
		if (!event.getOrganizerId().equals(organizerId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.ok(event);
	}

	@PutMapping("/{organizerId}/events/{eventId}")
	@Operation(summary = "Update Event", description = "Update event details (must belong to the organizer)")
	public ResponseEntity<EventResponse> updateEvent(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@Parameter(description = "Event ID") @PathVariable Long eventId,
			@RequestBody @Valid EventRequest request) {
		log.info("Update event {} for organizer: {}", eventId, organizerId);
		EventResponse response = eventService.updateEvent(eventId, request, organizerId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{organizerId}/events/{eventId}/status")
	@Operation(summary = "Update Event Status", description = "Update event status (must belong to the organizer)")
	public ResponseEntity<EventResponse> updateEventStatus(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@Parameter(description = "Event ID") @PathVariable Long eventId,
			@Parameter(description = "New status (UPCOMING, ONGOING, COMPLETED, CANCELLED)") @RequestParam String status) {
		log.info("Update event {} status to {} for organizer: {}", eventId, status, organizerId);
		EventResponse response = eventService.updateEventStatus(eventId, status, organizerId);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{organizerId}/events/{eventId}")
	@Operation(summary = "Delete Event", description = "Delete an event (must belong to the organizer)")
	public ResponseEntity<Void> deleteEvent(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@Parameter(description = "Event ID") @PathVariable Long eventId) {
		log.info("Delete event {} for organizer: {}", eventId, organizerId);
		eventService.deleteEvent(eventId, organizerId);
		return ResponseEntity.noContent().build();
	}

	// ===========================================
	// Event Registration Management APIs
	// ===========================================

	@GetMapping("/{organizerId}/events/{eventId}/registrations")
	@Operation(summary = "Get Event Registrations", description = "Get all registrations for a specific event")
	public ResponseEntity<List<EventRegistrationResponse>> getEventRegistrations(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@Parameter(description = "Event ID") @PathVariable Long eventId) {
		log.debug("Get registrations for event {} by organizer: {}", eventId, organizerId);
		List<EventRegistrationResponse> registrations = registrationService.getEventRegistrations(eventId, organizerId);
		return ResponseEntity.ok(registrations);
	}

	@GetMapping("/{organizerId}/registrations")
	@Operation(summary = "Get All Organizer Registrations", description = "Get all registrations for all events organized by the organizer")
	public ResponseEntity<List<EventRegistrationResponse>> getAllOrganizerRegistrations(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId) {
		log.debug("Get all registrations for organizer: {}", organizerId);
		List<EventRegistrationResponse> registrations = registrationService.getOrganizerRegistrations(organizerId);
		return ResponseEntity.ok(registrations);
	}

	@PutMapping("/{organizerId}/registrations/{registrationId}/payment-status")
	@Operation(summary = "Update Registration Payment Status", description = "Update payment status of a registration")
	public ResponseEntity<EventRegistrationResponse> updatePaymentStatus(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@Parameter(description = "Registration ID") @PathVariable Long registrationId,
			@Parameter(description = "Payment status (PENDING, PAID, REFUNDED)") @RequestParam String paymentStatus) {
		log.info("Update payment status for registration {} to {} by organizer: {}", registrationId, paymentStatus, organizerId);
		EventRegistrationResponse response = registrationService.updatePaymentStatus(registrationId, paymentStatus, organizerId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{organizerId}/registrations/{registrationId}/attendance")
	@Operation(summary = "Update Registration Attendance", description = "Update attendance status of a registration")
	public ResponseEntity<EventRegistrationResponse> updateAttendance(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@Parameter(description = "Registration ID") @PathVariable Long registrationId,
			@Parameter(description = "Attendance status (true/false)") @RequestParam Boolean attended) {
		log.info("Update attendance for registration {} to {} by organizer: {}", registrationId, attended, organizerId);
		EventRegistrationResponse response = registrationService.updateAttendance(registrationId, attended, organizerId);
		return ResponseEntity.ok(response);
	}

	// ===========================================
	// Statistics and Analytics APIs
	// ===========================================

	@GetMapping("/{organizerId}/statistics")
	@Operation(summary = "Get Organizer Statistics", description = "Get event statistics and analytics for the organizer dashboard")
	public ResponseEntity<EventStatisticsResponse> getOrganizerStatistics(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId) {
		log.debug("Get statistics for organizer: {}", organizerId);
		EventStatisticsResponse statistics = registrationService.getEventStatistics(organizerId);
		return ResponseEntity.ok(statistics);
	}

	// ===========================================
	// Profile Management APIs
	// ===========================================

	@GetMapping("/{organizerId}/profile")
	@Operation(summary = "Get Organizer Profile", description = "Get organizer's profile information including personal details and event statistics")
	public ResponseEntity<OrganizerProfileResponse> getOrganizerProfile(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId) {
		log.debug("Get profile for organizer: {}", organizerId);
		OrganizerProfileResponse profile = eventService.getOrganizerProfile(organizerId);
		return ResponseEntity.ok(profile);
	}

	@PutMapping("/{organizerId}/profile")
	@Operation(summary = "Update Organizer Profile", description = "Update organizer's profile (name, phone, city, state, about me)")
	public ResponseEntity<OrganizerProfileResponse> updateOrganizerProfile(
			@Parameter(description = "Organizer user ID") @PathVariable Long organizerId,
			@RequestBody @Valid OrganizerProfileUpdateRequest request) {
		log.info("Update profile for organizer: {}", organizerId);
		OrganizerProfileResponse updated = eventService.updateOrganizerProfile(organizerId, request);
		return ResponseEntity.ok(updated);
	}
}
