package com.scriptbliss.bandhan.match.dto;

import lombok.Data;

@Data
public class MatchResponse {
	private Long userId;
	private String name;
	private Integer age;
	private String city;
	private Double compatibilityScore;
}