package com.scriptbliss.bandhan.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.auth.dto.request.LoginRequest;
import com.scriptbliss.bandhan.auth.dto.request.RefreshTokenRequest;
import com.scriptbliss.bandhan.auth.dto.response.AuthResponse;
import com.scriptbliss.bandhan.auth.service.AuthService;
import com.scriptbliss.bandhan.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(ApiResponse.success("Login successful", response));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
		authService.logout(request.getRefreshToken());
		return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		AuthResponse response = authService.refreshToken(request.getRefreshToken());
		return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
	}
}