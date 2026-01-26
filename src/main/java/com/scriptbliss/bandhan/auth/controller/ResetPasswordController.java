package com.scriptbliss.bandhan.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.auth.dto.request.EmailRequest;
import com.scriptbliss.bandhan.auth.dto.request.ResetPasswordRequest;
import com.scriptbliss.bandhan.auth.dto.request.TokenValidationRequest;
import com.scriptbliss.bandhan.auth.service.PasswordResetService;
import com.scriptbliss.bandhan.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class ResetPasswordController {
	private final PasswordResetService passwordResetService;

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody EmailRequest request) {
		passwordResetService.requestPasswordReset(request.getEmail());
		return ResponseEntity.ok(ApiResponse.success("Password reset email sent if account exists."));
	}

	@PostMapping("/verify-reset-token")
	public ResponseEntity<ApiResponse<String>> verifyResetToken(@Valid @RequestBody TokenValidationRequest request) {
		String resetJWT = passwordResetService.verifyResetToken(request.getToken());
		return ResponseEntity
				.ok(ApiResponse.success("Reset token verified. You can now set your new password.", resetJWT));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestHeader("Authorization") String authHeader,
			@Valid @RequestBody ResetPasswordRequest request) {
		String jwt = authHeader.replace("Bearer ", "");
		passwordResetService.resetPasswordWithJWT(jwt, request.getNewPassword());
		return ResponseEntity.ok(ApiResponse.success("Password reset successfully."));
	}
}