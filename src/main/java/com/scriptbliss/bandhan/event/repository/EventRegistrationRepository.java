package com.scriptbliss.bandhan.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scriptbliss.bandhan.event.entity.EventRegistration;
import com.scriptbliss.bandhan.event.entity.EventRegistration.PaymentStatus;

/**
 * Repository interface for EventRegistration entity
 * Provides queries for event registration management
 */
@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
	
	/**
	 * Find all registrations for an event
	 * @param eventId Event ID
	 * @return List of registrations
	 */
	List<EventRegistration> findByEventId(Long eventId);
	
	/**
	 * Find all registrations by a user
	 * @param userId User ID
	 * @return List of registrations
	 */
	List<EventRegistration> findByUserId(Long userId);
	
	/**
	 * Find registration by user and event
	 * @param userId User ID
	 * @param eventId Event ID
	 * @return Optional registration
	 */
	Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);
	
	/**
	 * Count registrations for an event
	 * @param eventId Event ID
	 * @return Count of registrations
	 */
	long countByEventId(Long eventId);
	
	/**
	 * Count registrations by payment status for an event
	 * @param eventId Event ID
	 * @param paymentStatus Payment status
	 * @return Count of registrations
	 */
	long countByEventIdAndPaymentStatus(Long eventId, PaymentStatus paymentStatus);
	
	/**
	 * Find registrations for events organized by a specific organizer
	 * @param organizerId Organizer user ID
	 * @return List of registrations
	 */
	@Query("SELECT er FROM EventRegistration er WHERE er.event.organizer.id = :organizerId")
	List<EventRegistration> findByEventOrganizerId(@Param("organizerId") Long organizerId);
	
	/**
	 * Find registrations for a specific event organized by a specific organizer
	 * @param eventId Event ID
	 * @param organizerId Organizer user ID
	 * @return List of registrations
	 */
	@Query("SELECT er FROM EventRegistration er WHERE er.event.id = :eventId AND er.event.organizer.id = :organizerId")
	List<EventRegistration> findByEventIdAndOrganizerId(@Param("eventId") Long eventId, @Param("organizerId") Long organizerId);
}
