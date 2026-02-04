package com.scriptbliss.bandhan.auth.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scriptbliss.bandhan.profile.enums.Gender;
import com.scriptbliss.bandhan.profile.enums.MaritalStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s]).{8,}$",
			message = "Password must contain uppercase, lowercase, number and one special character")
	private String password;

	@NotBlank(message = "First name is required")
	@Size(max = 100)
	@JsonProperty("first_name")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 100)
	@JsonProperty("last_name")
	private String lastName;

	@NotBlank(message = "Phone is required")
	@Size(max = 20)
	private String phone;

	@JsonProperty("date_of_birth")
	@NotNull(message = "Date of birth is required")
	private LocalDate dateOfBirth;

	@NotNull(message = "Gender is required")
	private Gender gender;

	@NotBlank(message = "Religion is required")
	@Size(max = 50)
	private String religion;

	@Size(max = 50)
	private String caste;

	@NotBlank(message = "Occupation is required")
	@Size(max = 100)
	private String occupation;

	@NotBlank(message = "Education is required")
	@Size(max = 100)
	private String education;

	private BigDecimal income;

	@NotNull(message = "Marital status is required")
	@JsonProperty("marital_status")
	private MaritalStatus maritalStatus;

	@NotBlank(message = "City is required")
	@Size(max = 100)
	private String city;

	@NotBlank(message = "State is required")
	@Size(max = 100)
	private String state;

	@Size(max = 100)
	private String country;
}
