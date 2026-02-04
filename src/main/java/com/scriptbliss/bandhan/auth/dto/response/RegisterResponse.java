package com.scriptbliss.bandhan.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

	private UserInfo user;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfo {
		private Long id;
		private String email;
		private String firstName;
		private String lastName;
		private String userType;
		private String subscriptionTier;
	}
}
