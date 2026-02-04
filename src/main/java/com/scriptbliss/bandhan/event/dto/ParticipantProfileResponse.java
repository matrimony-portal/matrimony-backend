package com.scriptbliss.bandhan.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Read-only view of a registrant's profile for organizers: user id/name/email
 * and profile fields from {@code profiles} (age, gender, religion, etc.).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantProfileResponse {

	private Long userId;
	private String userName;
	private String userEmail;
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
}
