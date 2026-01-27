package com.scriptbliss.bandhan.event.entity;

import java.time.LocalDateTime;

import com.scriptbliss.bandhan.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Event Registration entity representing the event_registrations table.
 * Does not extend BaseEntity because this table doesn't have created_at/updated_at columns.
 */
@Entity
@Table(name = "event_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "event"})
public class EventRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@Column(name = "registration_date", nullable = false, updatable = false)
	private LocalDateTime registrationDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	@Column(nullable = false)
	private Boolean attended = false;

	@Column(columnDefinition = "TEXT")
	private String notes;

	/**
	 * Enum for payment status
	 */
	public enum PaymentStatus {
		PENDING, PAID, REFUNDED
	}

	@jakarta.persistence.PrePersist
	protected void onCreate() {
		if (registrationDate == null) {
			registrationDate = LocalDateTime.now();
		}
	}
}
