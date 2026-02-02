package com.scriptbliss.bandhan.admin.service;

import java.util.List;

import com.scriptbliss.bandhan.admin.dto.AdminCreateOrganizerRequest;
import com.scriptbliss.bandhan.admin.dto.AdminOrganizerDetailResponse;
import com.scriptbliss.bandhan.admin.dto.AdminOrganizerListResponse;
import com.scriptbliss.bandhan.admin.dto.AdminUpdateOrganizerRequest;
import com.scriptbliss.bandhan.event.dto.OrganizerProfileResponse;

public interface AdminOrganizerService {

	List<AdminOrganizerListResponse> getAllOrganizers(String status);

	AdminOrganizerDetailResponse getOrganizerById(Long organizerId);

	OrganizerProfileResponse createOrganizer(AdminCreateOrganizerRequest request);

	AdminOrganizerDetailResponse updateOrganizer(Long organizerId, AdminUpdateOrganizerRequest request);

	void updateOrganizerStatus(Long organizerId, String status);

	void deleteOrganizer(Long organizerId);
}
