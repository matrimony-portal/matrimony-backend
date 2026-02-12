package com.scriptbliss.bandhan.profile.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.profile.dto.request.UpdateProfileRequest;
import com.scriptbliss.bandhan.profile.dto.response.UserProfileResponse;
import com.scriptbliss.bandhan.profile.entity.Profile;
import com.scriptbliss.bandhan.profile.repository.ProfileRepository;
import com.scriptbliss.bandhan.shared.exception.BusinessException;
import com.scriptbliss.bandhan.shared.repository.PhotoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileServiceImpl implements ProfileService {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
	private final PhotoRepository photoRepository;
	private final ModelMapper modelMapper;

	@Override
	public UserProfileResponse getUserProfile(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new BusinessException("PROFILE_NOT_FOUND", "Profile not found"));

		// Map User fields to response
		UserProfileResponse response = modelMapper.map(user, UserProfileResponse.class);

		// Map Profile fields to response
		modelMapper.map(profile, response);
		response.setProfileId(profile.getId());

		return response;
	}

	@Override
	public UserProfileResponse getUserProfileWithPhotos(Long userId) {
		UserProfileResponse response = getUserProfile(userId);

		// Add photo URLs
		response.setPhotoUrls(photoRepository.findByUserIdOrderBySortOrder(userId).stream()
				.map(photo -> photo.getFilePath()).toList());

		return response;
	}

	@Override
	public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new BusinessException("PROFILE_NOT_FOUND", "Profile not found"));

		// Update user and profile using ModelMapper (only non-null fields)
		modelMapper.map(request, user);
		modelMapper.map(request, profile);

		User savedUser = userRepository.save(user);
		Profile savedProfile = profileRepository.save(profile);
		log.info("Profile updated for user: {}", user.getEmail());

		// Map response using ModelMapper
		UserProfileResponse response = modelMapper.map(savedUser, UserProfileResponse.class);
		modelMapper.map(savedProfile, response);
		response.setProfileId(savedProfile.getId());

		return response;
	}
}