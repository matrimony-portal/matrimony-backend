package com.scriptbliss.bandhan.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.profile.dto.request.UpdateProfileRequest;
import com.scriptbliss.bandhan.profile.dto.response.UserProfileResponse;
import com.scriptbliss.bandhan.profile.service.ProfileService;
import com.scriptbliss.bandhan.shared.dto.ApiResponse;
import com.scriptbliss.bandhan.shared.security.CustomUserDetailsService.CustomUserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/profile")
@RestController
@RequiredArgsConstructor
public class ProfileController {
	private final ProfileService profileService;

	@GetMapping
	public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
			@AuthenticationPrincipal CustomUserPrincipal principal) {
		UserProfileResponse profile = profileService.getUserProfile(principal.getUserId());
		return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
	}

	@PutMapping
	public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
			@AuthenticationPrincipal CustomUserPrincipal principal, @Valid @RequestBody UpdateProfileRequest request) {
		UserProfileResponse profile = profileService.updateProfile(principal.getUserId(), request);
		return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
	}
}