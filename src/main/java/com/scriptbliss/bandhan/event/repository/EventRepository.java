package com.scriptbliss.bandhan.event.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scriptbliss.bandhan.event.entity.Event;
import com.scriptbliss.bandhan.event.entity.Event.EventStatus;

/**
 * Repository interface for Event entity
 * Follows Repository Pattern and provides custom queries
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	
	/**
	 * Find all events by organizer
	 * @param organizerId Organizer user ID
	 * @return List of events
	 */
	List<Event> findByOrganizerId(Long organizerId);
	
	/**
	 * Find events by status
	 * @param status Event status
	 * @return List of events
	 */
	List<Event> findByStatus(EventStatus status);
	
	/**
	 * Find events by city
	 * @param city City name
	 * @return List of events
	 */
	List<Event> findByCity(String city);
	
	/**
	 * Find upcoming events
	 * @param currentDate Current date and time
	 * @return List of upcoming events
	 */
	@Query("SELECT e FROM Event e WHERE e.eventDate > :currentDate AND e.status = 'UPCOMING' ORDER BY e.eventDate ASC")
	List<Event> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate);
	
	/**
	 * Find events by organizer and status
	 * @param organizerId Organizer user ID
	 * @param status Event status
	 * @return List of events
	 */
	List<Event> findByOrganizerIdAndStatus(Long organizerId, EventStatus status);
}
