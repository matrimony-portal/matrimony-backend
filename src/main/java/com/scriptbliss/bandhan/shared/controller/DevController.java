package com.scriptbliss.bandhan.shared.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.shared.config.DataLoader;
import com.scriptbliss.bandhan.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dev")
@Profile("dev")
@RequiredArgsConstructor
public class DevController {

	private final DataLoader dataLoader;

	@PostMapping("/create-test-data")
	public ResponseEntity<ApiResponse<Void>> createTestData() {
		try {
			dataLoader.createTestData();
			return ResponseEntity.ok(ApiResponse.success(null));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ApiResponse.error("DATA_CREATION_FAILED", "Failed to create test data: " + e.getMessage()));
		}
	}
}