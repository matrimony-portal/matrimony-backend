package com.scriptbliss.bandhan.auth.entity;

import java.time.LocalDateTime;

import com.scriptbliss.bandhan.auth.enums.TokenType;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Email verification token entity.
 * Does not extend BaseEntity because this table doesn't have updated_at column.
 */
@Entity
@Table(name = "verification_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 255)
	private String token;

	@Column(nullable = false, length = 255)
	private String email;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "token_type", nullable = false, length = 20)
	private TokenType tokenType;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "used_at")
	private LocalDateTime usedAt;

	/**
	 * Check if token has been used (derived from usedAt field)
	 */
	public boolean isUsed() {
		return usedAt != null;
	}

	/**
	 * Mark token as used or unused. Sets usedAt when true, clears when false.
	 */
	public void setUsed(boolean used) {
		this.usedAt = used ? LocalDateTime.now() : null;
	}

	@jakarta.persistence.PrePersist
	protected void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}
}
