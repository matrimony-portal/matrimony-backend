package com.scriptbliss.bandhan.auth.dto.response;

import com.scriptbliss.bandhan.auth.enums.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
	private String token;
	private String refreshToken;
	private long expiresIn;
	private UserInfo user;

	@Data
	@Builder
	public static class UserInfo {
		private Long id;
		private String email;
		private String firstName;
		private String lastName;
		private UserRole role;
		/** "FREE" or "PREMIUM" for USER role; null for ADMIN/EVENT_ORGANIZER */
		private String planType;
	}
}