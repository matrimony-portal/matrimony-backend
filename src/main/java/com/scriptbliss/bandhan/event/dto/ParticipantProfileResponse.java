package com.scriptbliss.bandhan.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for viewing a participant's (registrant's) profile by an event organizer.
 * Contains user and profile data for read-only display.
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
