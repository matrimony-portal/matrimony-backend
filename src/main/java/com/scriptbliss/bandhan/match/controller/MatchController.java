package com.scriptbliss.bandhan.match.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.match.dto.MatchResponse;
import com.scriptbliss.bandhan.match.service.MatchService;
import com.scriptbliss.bandhan.shared.security.CustomUserDetailsService.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

	private final MatchService matchService;

	@GetMapping("/discover")
	public List<MatchResponse> getMatches(@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestParam(defaultValue = "10") int limit) {
		return matchService.findPotentialMatches(principal.getUserId(), limit);
	}

	@GetMapping
	public List<MatchResponse> getMyMatches(@AuthenticationPrincipal CustomUserPrincipal principal) {
		return matchService.getMyMatches(principal.getUserId());
	}
}