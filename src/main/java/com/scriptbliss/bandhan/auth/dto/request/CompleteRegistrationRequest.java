package com.scriptbliss.bandhan.auth.dto.request;

import com.scriptbliss.bandhan.auth.enums.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompleteRegistrationRequest {
	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s]).{8,}$",
			message = "Password must contain uppercase, lowercase, number and one special character")
	private String password;

	@NotBlank(message = "First name is required")
	@Size(max = 100, message = "First name must not exceed 100 characters")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 100, message = "Last name must not exceed 100 characters")
	private String lastName;

	@Size(max = 20, message = "Phone must not exceed 20 characters")
	private String phone;

	@NotNull(message = "Role is required")
	private UserRole role;
}