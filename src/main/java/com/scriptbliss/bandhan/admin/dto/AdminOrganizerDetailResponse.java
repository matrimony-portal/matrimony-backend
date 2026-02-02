package com.scriptbliss.bandhan.admin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Admin view of an organizer: organizer profile fields plus status and createdAt. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrganizerDetailResponse {

	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String fullName;

	private LocalDate dateOfBirth;
	private Integer age;
	private String gender;
	private String religion;
	private String caste;
	private String occupation;
	private String education;
	private String city;
	private String state;
	private String country;
	private String aboutMe;

	private Long totalEvents;
	private Long upcomingEvents;
	private Long completedEvents;

	private String status;
	private LocalDateTime createdAt;
}
