package com.scriptbliss.bandhan.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for PUT organizer profile. Updates users + profiles: firstName, lastName required;
 * phone, city, state, aboutMe optional (profiles updated via native query where provided).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerProfileUpdateRequest {

	@NotBlank(message = "First name is required")
	private String firstName;

	@NotBlank(message = "Last name is required")
	private String lastName;

	private String phone;
	private String city;
	private String state;
	private String aboutMe;
}
