package com.scriptbliss.bandhan.profile.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.profile.enums.Gender;
import com.scriptbliss.bandhan.profile.enums.MaritalStatus;
import com.scriptbliss.bandhan.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Profile extends BaseEntity {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "date_of_birth", nullable = false)
	private LocalDate dateOfBirth;

	@Enumerated(EnumType.STRING)
	@Column(length = 10, nullable = false)
	private Gender gender;

	@Column(length = 50)
	private String religion;

	@Column(length = 50)
	private String caste;

	@Column(length = 100)
	private String occupation;

	@Column(length = 100)
	private String education;

	@Column(precision = 10, scale = 2)
	private BigDecimal income;

	@Enumerated(EnumType.STRING)
	@Column(name = "marital_status", length = 20)
	@Builder.Default
	private MaritalStatus maritalStatus = MaritalStatus.SINGLE;

	@Column(name = "height_cm")
	private Integer heightCm;

	@Column(name = "weight_kg")
	private Integer weightKg;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String state;

	@Column(length = 100)
	@Builder.Default
	private String country = "India";

	@Column(name = "about_me", columnDefinition = "TEXT")
	private String aboutMe;

	@Column(columnDefinition = "TEXT")
	private String preferences;

	@Column(length = 100)
	private String citizenship;

	@Column(length = 200)
	private String college;

	@Column(length = 200)
	private String company;

	@Column(name = "is_verified", nullable = false)
	@Builder.Default
	private boolean isVerified = false;
}