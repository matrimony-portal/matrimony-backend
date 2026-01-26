package com.scriptbliss.bandhan.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.auth.dto.request.CompleteRegistrationRequest;
import com.scriptbliss.bandhan.auth.dto.request.EmailRequest;
import com.scriptbliss.bandhan.auth.dto.request.TokenValidationRequest;
import com.scriptbliss.bandhan.auth.service.RegistrationService;
import com.scriptbliss.bandhan.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class RegistrationController {
	private final RegistrationService registrationService;

	@PostMapping("/start-registration")
	public ResponseEntity<ApiResponse<Void>> startRegistration(@Valid @RequestBody EmailRequest request) {
		registrationService.startRegistration(request.getEmail());
		return ResponseEntity
				.ok(ApiResponse.success("Verification email sent. Please check your email to continue registration."));
	}

	@PostMapping("/verify-email")
	public ResponseEntity<ApiResponse<String>> verifyEmail(@Valid @RequestBody TokenValidationRequest request) {
		String registrationJWT = registrationService.verifyEmailForRegistration(request.getToken());
		return ResponseEntity.ok(ApiResponse
				.success("Email verified successfully. You can now complete your registration.", registrationJWT));
	}

	@PostMapping("/complete-registration")
	public ResponseEntity<ApiResponse<Void>> completeRegistration(@RequestHeader("Authorization") String authHeader,
			@Valid @RequestBody CompleteRegistrationRequest request) {
		String jwt = authHeader.replace("Bearer ", "");
		registrationService.completeRegistration(jwt, request);
		return ResponseEntity.ok(ApiResponse.success("Registration completed successfully. You can now login."));
	}
}
