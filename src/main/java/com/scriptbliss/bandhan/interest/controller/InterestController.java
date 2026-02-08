package com.scriptbliss.bandhan.interest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.interest.enums.InterestType;
import com.scriptbliss.bandhan.interest.service.InterestService;
import com.scriptbliss.bandhan.shared.security.CustomUserDetailsService.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/interests")
@RequiredArgsConstructor
public class InterestController {

	private final InterestService interestService;

	@PostMapping("/like/{userId}")
	public ResponseEntity<Void> likeUser(@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long userId) {
		interestService.expressInterest(principal.getUserId(), userId, InterestType.LIKE);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/pass/{userId}")
	public ResponseEntity<Void> passUser(@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long userId) {
		interestService.expressInterest(principal.getUserId(), userId, InterestType.PASS);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/liked")
	public List<Long> getLikedUsers(@AuthenticationPrincipal CustomUserPrincipal principal) {
		return interestService.getLikedUsers(principal.getUserId());
	}
}