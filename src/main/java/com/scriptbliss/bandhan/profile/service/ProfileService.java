package com.scriptbliss.bandhan.profile.service;

import com.scriptbliss.bandhan.profile.dto.request.UpdateProfileRequest;
import com.scriptbliss.bandhan.profile.dto.response.UserProfileResponse;

public interface ProfileService {
	UserProfileResponse getUserProfile(Long userId);

	UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
}