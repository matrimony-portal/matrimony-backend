package com.scriptbliss.bandhan.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.admin.dto.AdminCreateOrganizerRequest;
import com.scriptbliss.bandhan.admin.dto.AdminOrganizerDetailResponse;
import com.scriptbliss.bandhan.admin.dto.AdminOrganizerListResponse;
import com.scriptbliss.bandhan.admin.dto.AdminUpdateOrganizerRequest;
import com.scriptbliss.bandhan.admin.service.AdminOrganizerService;
import com.scriptbliss.bandhan.event.dto.OrganizerProfileResponse;
import com.scriptbliss.bandhan.shared.dto.ApiResponse;
import com.scriptbliss.bandhan.shared.security.CustomUserDetailsService.CustomUserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/organizers")
@RequiredArgsConstructor
public class AdminOrganizerController {

	private final AdminOrganizerService adminOrganizerService;

	private void ensureAdmin(CustomUserPrincipal principal) {
		if (principal == null || !"ADMIN".equals(principal.getRole())) {
			throw new AccessDeniedException("Admin access required");
		}
	}

	@GetMapping
	public ResponseEntity<ApiResponse<java.util.List<AdminOrganizerListResponse>>> getAllOrganizers(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestParam(required = false) String status) {
		ensureAdmin(principal);
		java.util.List<AdminOrganizerListResponse> list = adminOrganizerService.getAllOrganizers(status);
		return ResponseEntity.ok(ApiResponse.success(list));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AdminOrganizerDetailResponse>> getOrganizerById(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long id) {
		ensureAdmin(principal);
		AdminOrganizerDetailResponse dto = adminOrganizerService.getOrganizerById(id);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<OrganizerProfileResponse>> createOrganizer(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@Valid @RequestBody AdminCreateOrganizerRequest request) {
		ensureAdmin(principal);
		OrganizerProfileResponse created = adminOrganizerService.createOrganizer(request);
		return ResponseEntity.ok(ApiResponse.success("Organizer created successfully", created));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<AdminOrganizerDetailResponse>> updateOrganizer(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long id,
			@Valid @RequestBody AdminUpdateOrganizerRequest request) {
		ensureAdmin(principal);
		AdminOrganizerDetailResponse updated = adminOrganizerService.updateOrganizer(id, request);
		return ResponseEntity.ok(ApiResponse.success("Organizer updated successfully", updated));
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<ApiResponse<Void>> updateOrganizerStatus(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long id,
			@RequestParam String status) {
		ensureAdmin(principal);
		adminOrganizerService.updateOrganizerStatus(id, status);
		return ResponseEntity.ok(ApiResponse.success("Status updated"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteOrganizer(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long id) {
		ensureAdmin(principal);
		adminOrganizerService.deleteOrganizer(id);
		return ResponseEntity.ok(ApiResponse.success("Organizer deleted"));
	}
}
