package com.scriptbliss.bandhan.admin.dto;

import java.time.LocalDate;

import com.scriptbliss.bandhan.profile.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AdminCreateOrganizerRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	private String password;

	@NotBlank(message = "First name is required")
	@Size(max = 100)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 100)
	private String lastName;

	@Size(max = 20)
	private String phone;

	@NotNull(message = "Date of birth is required")
	private LocalDate dateOfBirth;

	@NotNull(message = "Gender is required")
	private Gender gender;

	@Size(max = 100)
	private String city;

	@Size(max = 100)
	private String state;

	@Size(max = 2000)
	private String aboutMe;

	@Pattern(regexp = "ACTIVE|INACTIVE|BLOCKED", message = "Status must be ACTIVE, INACTIVE, or BLOCKED")
	private String status; // default ACTIVE if null
}
