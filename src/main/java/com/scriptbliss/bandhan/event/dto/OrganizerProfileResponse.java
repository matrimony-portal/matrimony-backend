package com.scriptbliss.bandhan.event.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for organizer profile response
 * Contains organizer's personal and professional information
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerProfileResponse {

	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String fullName;

	// Profile information (from profiles table if exists)
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

	// Event statistics
	private Long totalEvents;
	private Long upcomingEvents;
	private Long completedEvents;
}
