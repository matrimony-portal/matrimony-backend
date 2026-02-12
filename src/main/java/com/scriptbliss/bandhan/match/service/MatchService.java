package com.scriptbliss.bandhan.match.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.scriptbliss.bandhan.match.dto.MatchResponse;
import com.scriptbliss.bandhan.match.entity.Match;
import com.scriptbliss.bandhan.match.repository.MatchRepository;
import com.scriptbliss.bandhan.profile.entity.Profile;
import com.scriptbliss.bandhan.profile.enums.Gender;
import com.scriptbliss.bandhan.profile.repository.ProfileRepository;
import com.scriptbliss.bandhan.shared.repository.PhotoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

	private final ProfileRepository profileRepository;
	private final MatchRepository matchRepository;
	private final PhotoRepository photoRepository;

	public List<MatchResponse> findPotentialMatches(Long userId, int limit) {
		Profile currentProfile = profileRepository.findByUserId(userId).orElseThrow();

		// Basic matching: opposite gender, similar age range
		Gender targetGender = currentProfile.getGender() == Gender.MALE ? Gender.FEMALE : Gender.MALE;

		List<Profile> potentialMatches = profileRepository.findPotentialMatches(targetGender, userId,
				PageRequest.of(0, limit));

		return potentialMatches.stream().map(profile -> {
			MatchResponse response = new MatchResponse();
			response.setUserId(profile.getUser().getId());
			response.setName(profile.getUser().getFirstName() + " " + profile.getUser().getLastName());
			response.setAge(calculateAge(profile.getDateOfBirth()));
			response.setCity(profile.getCity());
			response.setProfilePhotoUrl(photoRepository.findByUserIdAndIsPrimaryTrue(profile.getUser().getId())
					.map(photo -> photo.getFilePath()).orElse(null));
			response.setCompatibilityScore(calculateCompatibility(currentProfile, profile));
			return response;
		}).collect(Collectors.toList());
	}

	public List<MatchResponse> getMyMatches(Long userId) {
		List<Match> matches = matchRepository.findByUserId(userId);
		return matches.stream().map(match -> {
			Long otherUserId = match.getUser1Id().equals(userId) ? match.getUser2Id() : match.getUser1Id();
			Profile otherProfile = profileRepository.findByUserId(otherUserId).orElse(null);
			if (otherProfile == null)
				return null;

			MatchResponse response = new MatchResponse();
			response.setUserId(otherProfile.getUser().getId());
			response.setName(otherProfile.getUser().getFirstName() + " " + otherProfile.getUser().getLastName());
			response.setAge(calculateAge(otherProfile.getDateOfBirth()));
			response.setCity(otherProfile.getCity());
			response.setProfilePhotoUrl(photoRepository.findByUserIdAndIsPrimaryTrue(otherProfile.getUser().getId())
					.map(photo -> photo.getFilePath()).orElse(null));
			response.setCompatibilityScore(match.getCompatibilityScore());
			return response;
		}).filter(response -> response != null).collect(Collectors.toList());
	}

	private int calculateAge(LocalDate dateOfBirth) {
		if (dateOfBirth == null)
			return 25; // Default age
		return Period.between(dateOfBirth, LocalDate.now()).getYears();
	}

	private Double calculateCompatibility(Profile profile1, Profile profile2) {
		double score = 0.5; // Base score

		// Age compatibility (closer age = higher score)
		int age1 = calculateAge(profile1.getDateOfBirth());
		int age2 = calculateAge(profile2.getDateOfBirth());
		int ageDiff = Math.abs(age1 - age2);
		score += Math.max(0, (10 - ageDiff) * 0.05);

		// City match bonus
		if (profile1.getCity() != null && profile1.getCity().equals(profile2.getCity())) {
			score += 0.2;
		}

		// Religion match bonus
		if (profile1.getReligion() != null && profile1.getReligion().equals(profile2.getReligion())) {
			score += 0.1;
		}

		return Math.min(1.0, score);
	}
}