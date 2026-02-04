package com.scriptbliss.bandhan.admin.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateOrganizerRequest {

	@Size(max = 100)
	private String firstName;

	@Size(max = 100)
	private String lastName;

	@Size(max = 20)
	private String phone;

	@Size(max = 100)
	private String city;

	@Size(max = 100)
	private String state;

	@Size(max = 2000)
	private String aboutMe;

	@Pattern(regexp = "ACTIVE|INACTIVE|BLOCKED", message = "Status must be ACTIVE, INACTIVE, or BLOCKED")
	private String status;

	@Size(min = 8, message = "New password must be at least 8 characters if provided")
	private String newPassword;
}
