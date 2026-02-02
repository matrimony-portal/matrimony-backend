package com.scriptbliss.bandhan.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrganizerListResponse {

	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String fullName;
	private String phone;
	private String status;
	private String city;
	private String state;
	private Long eventCount;
	private LocalDateTime createdAt;
}
