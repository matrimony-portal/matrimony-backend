package com.scriptbliss.bandhan.auth.entity;

import java.time.LocalDateTime;

import com.scriptbliss.bandhan.auth.enums.TokenType;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Verification token entity (email verification, password reset).
 * Extends BaseEntity (id, createdAt, updatedAt). usedAt = when token was used; isUsed() derived from it.
 */
@Entity
@Table(name = "verification_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VerificationToken extends BaseEntity {

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

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "used_at")
	private LocalDateTime usedAt;

	/** Whether the token has been used (derived from usedAt). */
	public boolean isUsed() {
		return usedAt != null;
	}

	/** Set used state: sets or clears usedAt. */
	public void setUsed(boolean used) {
		this.usedAt = used ? LocalDateTime.now() : null;
	}
}
