package com.scriptbliss.bandhan.profile.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.scriptbliss.bandhan.profile.enums.Gender;
import com.scriptbliss.bandhan.profile.enums.MaritalStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
	// User fields
	@Size(max = 100, message = "First name must not exceed 100 characters")
	private String firstName;

	@Size(max = 100, message = "Last name must not exceed 100 characters")
	private String lastName;

	@Size(max = 20, message = "Phone must not exceed 20 characters")
	private String phone;

	// Profile fields
	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	private Gender gender;

	@Size(max = 50, message = "Religion must not exceed 50 characters")
	private String religion;

	@Size(max = 50, message = "Caste must not exceed 50 characters")
	private String caste;

	@Size(max = 100, message = "Occupation must not exceed 100 characters")
	private String occupation;

	@Size(max = 100, message = "Education must not exceed 100 characters")
	private String education;

	@Min(value = 0, message = "Income must be non-negative")
	private BigDecimal income;

	private MaritalStatus maritalStatus;

	@Min(value = 50, message = "Height must be at least 50 cm")
	@Max(value = 300, message = "Height must not exceed 300 cm")
	private Integer heightCm;

	@Min(value = 20, message = "Weight must be at least 20 kg")
	@Max(value = 500, message = "Weight must not exceed 500 kg")
	private Integer weightKg;

	@Size(max = 100, message = "City must not exceed 100 characters")
	private String city;

	@Size(max = 100, message = "State must not exceed 100 characters")
	private String state;

	@Size(max = 100, message = "Country must not exceed 100 characters")
	private String country;

	@Size(max = 2000, message = "About me must not exceed 2000 characters")
	private String aboutMe;

	@Size(max = 2000, message = "Preferences must not exceed 2000 characters")
	private String preferences;
}