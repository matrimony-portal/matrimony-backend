package com.scriptbliss.bandhan.profile.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.scriptbliss.bandhan.auth.enums.AccountStatus;
import com.scriptbliss.bandhan.auth.enums.UserRole;
import com.scriptbliss.bandhan.profile.enums.Gender;
import com.scriptbliss.bandhan.profile.enums.MaritalStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
	// User fields
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private UserRole role;
	private AccountStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// Profile fields
	private Long profileId;
	private LocalDate dateOfBirth;
	private Gender gender;
	private String religion;
	private String caste;
	private String occupation;
	private String education;
	private BigDecimal income;
	private MaritalStatus maritalStatus;
	private Integer heightCm;
	private Integer weightKg;
	private String city;
	private String state;
	private String country;
	private String aboutMe;
	private String preferences;
	private String citizenship;
	private String college;
	private String company;
	private boolean isVerified;
}