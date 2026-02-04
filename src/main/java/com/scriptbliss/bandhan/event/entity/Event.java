package com.scriptbliss.bandhan.event.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity for the {@code events} table. Extends {@link com.scriptbliss.bandhan.shared.entity.BaseEntity}
 * (id, createdAt, updatedAt).
 *
 * <p>Organizer is a {@link com.scriptbliss.bandhan.auth.entity.User} with role EVENT_ORGANIZER or ADMIN.
 * Status lifecycle: UPCOMING → ONGOING → COMPLETED, or CANCELLED. eventType examples: SPEED_DATING,
 * COFFEE_MEETUP, DINNER, CULTURAL.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "organizer")
public class Event extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organizer_id", nullable = false)
	private User organizer;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "event_date", nullable = false)
	private LocalDateTime eventDate;

	@Column(nullable = false, length = 255)
	private String venue;

	@Column(nullable = false, length = 100)
	private String city;

	@Column(nullable = false, length = 100)
	private String state;

	/** e.g. SPEED_DATING, COFFEE_MEETUP, DINNER, CULTURAL. Default SPEED_DATING. */
	@Column(name = "event_type", length = 50)
	private String eventType = "SPEED_DATING";

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(name = "max_participants")
	private Integer maxParticipants;

	@Column(name = "registration_fee", precision = 10, scale = 2)
	private BigDecimal registrationFee = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EventStatus status = EventStatus.UPCOMING;

	public enum EventStatus {
		UPCOMING, ONGOING, COMPLETED, CANCELLED
	}

	@jakarta.persistence.PrePersist
	protected void onCreate() {
		if (getCreatedAt() == null) {
			setCreatedAt(java.time.LocalDateTime.now());
		}
		setUpdatedAt(java.time.LocalDateTime.now());
	}

	@jakarta.persistence.PreUpdate
	protected void onUpdate() {
		setUpdatedAt(java.time.LocalDateTime.now());
	}
}
