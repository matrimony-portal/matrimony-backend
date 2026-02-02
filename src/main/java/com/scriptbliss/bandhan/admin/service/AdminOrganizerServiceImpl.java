package com.scriptbliss.bandhan.admin.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scriptbliss.bandhan.admin.dto.AdminCreateOrganizerRequest;
import com.scriptbliss.bandhan.admin.dto.AdminOrganizerDetailResponse;
import com.scriptbliss.bandhan.admin.dto.AdminOrganizerListResponse;
import com.scriptbliss.bandhan.admin.dto.AdminUpdateOrganizerRequest;
import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.AccountStatus;
import com.scriptbliss.bandhan.auth.enums.UserRole;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.event.dto.OrganizerProfileResponse;
import com.scriptbliss.bandhan.event.entity.Event;
import com.scriptbliss.bandhan.event.repository.EventRepository;
import com.scriptbliss.bandhan.event.service.EventService;
import com.scriptbliss.bandhan.profile.entity.Profile;
import com.scriptbliss.bandhan.profile.repository.ProfileRepository;
import com.scriptbliss.bandhan.shared.exception.BusinessException;
import com.scriptbliss.bandhan.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminOrganizerServiceImpl implements AdminOrganizerService {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
	private final EventRepository eventRepository;
	private final EventService eventService;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public List<AdminOrganizerListResponse> getAllOrganizers(String status) {
		List<User> organizers = userRepository.findByRole(UserRole.EVENT_ORGANIZER);
		return organizers.stream()
				.filter(u -> status == null || status.isBlank() || u.getStatus().name().equalsIgnoreCase(status))
				.map(this::toListResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public AdminOrganizerDetailResponse getOrganizerById(Long organizerId) {
		User user = userRepository.findById(organizerId)
				.orElseThrow(() -> new ResourceNotFoundException("Organizer not found with ID: " + organizerId));
		if (!user.getRole().equals(UserRole.EVENT_ORGANIZER)) {
			throw new ResourceNotFoundException("User is not an event organizer");
		}
		OrganizerProfileResponse base = eventService.getOrganizerProfile(organizerId);
		return AdminOrganizerDetailResponse.builder()
				.id(base.getId())
				.email(base.getEmail())
				.firstName(base.getFirstName())
				.lastName(base.getLastName())
				.phone(base.getPhone())
				.fullName(base.getFullName())
				.dateOfBirth(base.getDateOfBirth())
				.age(base.getAge())
				.gender(base.getGender())
				.religion(base.getReligion())
				.caste(base.getCaste())
				.occupation(base.getOccupation())
				.education(base.getEducation())
				.city(base.getCity())
				.state(base.getState())
				.country(base.getCountry())
				.aboutMe(base.getAboutMe())
				.totalEvents(base.getTotalEvents())
				.upcomingEvents(base.getUpcomingEvents())
				.completedEvents(base.getCompletedEvents())
				.status(user.getStatus().name())
				.createdAt(user.getCreatedAt())
				.build();
	}

	@Override
	public OrganizerProfileResponse createOrganizer(AdminCreateOrganizerRequest request) {
		if (userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
			throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email already registered");
		}
		AccountStatus accStatus = parseStatus(request.getStatus(), AccountStatus.ACTIVE);

		User user = User.builder()
				.email(request.getEmail().trim().toLowerCase())
				.password(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName().trim())
				.lastName(request.getLastName().trim())
				.phone(request.getPhone() != null ? request.getPhone().trim() : null)
				.role(UserRole.EVENT_ORGANIZER)
				.status(accStatus)
				.build();
		User savedUser = userRepository.save(user);

		Profile profile = Profile.builder()
				.user(savedUser)
				.dateOfBirth(request.getDateOfBirth())
				.gender(request.getGender())
				.city(request.getCity() != null ? request.getCity().trim() : null)
				.state(request.getState() != null ? request.getState().trim() : null)
				.country("India")
				.aboutMe(request.getAboutMe() != null ? request.getAboutMe().trim() : null)
				.isVerified(false)
				.build();
		profileRepository.save(profile);

		log.info("Admin created organizer: {} ({})", savedUser.getEmail(), savedUser.getId());
		return eventService.getOrganizerProfile(savedUser.getId());
	}

	@Override
	public AdminOrganizerDetailResponse updateOrganizer(Long organizerId, AdminUpdateOrganizerRequest request) {
		User user = userRepository.findById(organizerId)
				.orElseThrow(() -> new ResourceNotFoundException("Organizer not found with ID: " + organizerId));
		if (!user.getRole().equals(UserRole.EVENT_ORGANIZER)) {
			throw new ResourceNotFoundException("User is not an event organizer");
		}

		if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
			user.setFirstName(request.getFirstName().trim());
		}
		if (request.getLastName() != null && !request.getLastName().isBlank()) {
			user.setLastName(request.getLastName().trim());
		}
		if (request.getPhone() != null) {
			user.setPhone(request.getPhone().trim().isEmpty() ? null : request.getPhone().trim());
		}
		if (request.getStatus() != null && !request.getStatus().isBlank()) {
			user.setStatus(parseStatus(request.getStatus(), user.getStatus()));
		}
		if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		}
		userRepository.save(user);

		Profile profile = profileRepository.findByUserId(organizerId).orElse(null);
		if (profile != null) {
			if (request.getCity() != null) profile.setCity(request.getCity().trim().isEmpty() ? null : request.getCity().trim());
			if (request.getState() != null) profile.setState(request.getState().trim().isEmpty() ? null : request.getState().trim());
			if (request.getAboutMe() != null) profile.setAboutMe(request.getAboutMe().trim().isEmpty() ? null : request.getAboutMe().trim());
			profileRepository.save(profile);
		}

		log.info("Admin updated organizer: {}", organizerId);
		return getOrganizerById(organizerId);
	}

	@Override
	public void updateOrganizerStatus(Long organizerId, String status) {
		User user = userRepository.findById(organizerId)
				.orElseThrow(() -> new ResourceNotFoundException("Organizer not found with ID: " + organizerId));
		if (!user.getRole().equals(UserRole.EVENT_ORGANIZER)) {
			throw new ResourceNotFoundException("User is not an event organizer");
		}
		user.setStatus(parseStatus(status, user.getStatus()));
		userRepository.save(user);
		log.info("Admin set organizer {} status to {}", organizerId, status);
	}

	@Override
	public void deleteOrganizer(Long organizerId) {
		User user = userRepository.findById(organizerId)
				.orElseThrow(() -> new ResourceNotFoundException("Organizer not found with ID: " + organizerId));
		if (!user.getRole().equals(UserRole.EVENT_ORGANIZER)) {
			throw new ResourceNotFoundException("User is not an event organizer");
		}
		List<Event> events = eventRepository.findByOrganizerId(organizerId);
		if (!events.isEmpty()) {
			throw new BusinessException("ORGANIZER_HAS_EVENTS", "Cannot delete organizer with existing events. Block the account instead.");
		}
		userRepository.delete(user);
		log.info("Admin deleted organizer: {}", organizerId);
	}

	private AdminOrganizerListResponse toListResponse(User u) {
		List<Event> events = eventRepository.findByOrganizerId(u.getId());
		String city = null;
		String state = null;
		var prof = profileRepository.findByUserId(u.getId());
		if (prof.isPresent()) {
			city = prof.get().getCity();
			state = prof.get().getState();
		}
		return AdminOrganizerListResponse.builder()
				.id(u.getId())
				.email(u.getEmail())
				.firstName(u.getFirstName())
				.lastName(u.getLastName())
				.fullName(u.getFirstName() + " " + u.getLastName())
				.phone(u.getPhone())
				.status(u.getStatus().name())
				.city(city)
				.state(state)
				.eventCount((long) events.size())
				.createdAt(u.getCreatedAt())
				.build();
	}

	private AccountStatus parseStatus(String s, AccountStatus fallback) {
		if (s == null || s.isBlank()) return fallback;
		try {
			return AccountStatus.valueOf(s.toUpperCase());
		} catch (IllegalArgumentException e) {
			return fallback;
		}
	}
}
