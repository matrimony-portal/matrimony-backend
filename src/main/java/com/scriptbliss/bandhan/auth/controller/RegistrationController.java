package com.scriptbliss.bandhan.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.auth.dto.request.RegisterRequest;
import com.scriptbliss.bandhan.auth.service.RegistrationService;
import com.scriptbliss.bandhan.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/register")
@RestController
@RequiredArgsConstructor
public class RegistrationController {
	private final RegistrationService registrationService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
		registrationService.registerUser(request);
		return ResponseEntity
				.ok(ApiResponse.success("User registered successfully. Please check your email for verification."));
	}

	@PostMapping("/verify-email")
	public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
		registrationService.verifyEmail(token);
		return ResponseEntity.ok(ApiResponse.success("Email verified successfully. You can now login."));
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
		registrationService.resendVerificationEmail(email);
		return ResponseEntity.ok(ApiResponse.success("Verification email sent successfully."));
	}
}
