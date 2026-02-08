package com.scriptbliss.bandhan.interest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.interest.entity.Interest;
import com.scriptbliss.bandhan.interest.enums.InterestType;
import com.scriptbliss.bandhan.interest.repository.InterestRepository;
import com.scriptbliss.bandhan.match.entity.Match;
import com.scriptbliss.bandhan.match.repository.MatchRepository;
import com.scriptbliss.bandhan.shared.exception.IllegalStateException;
import com.scriptbliss.bandhan.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestService {

	private final InterestRepository interestRepository;
	private final MatchRepository matchRepository;
	private final UserRepository userRepository;

	public void expressInterest(Long fromUserId, Long toUserId, InterestType type) {
		// Validate users exist
		userRepository.findById(fromUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + fromUserId));
		userRepository.findById(toUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + toUserId));

		// Validate not expressing interest in self
		if (fromUserId.equals(toUserId)) {
			throw new IllegalArgumentException("Cannot express interest in yourself");
		}

		// Check if interest already exists
		if (interestRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)) {
			throw new IllegalStateException("Interest already expressed for this user");
		}

		Interest interest = new Interest();
		interest.setFromUserId(fromUserId);
		interest.setToUserId(toUserId);
		interest.setType(type);

		interestRepository.save(interest);

		// Check for mutual like and create match
		if (type == InterestType.LIKE && interestRepository.existsByFromUserIdAndToUserId(toUserId, fromUserId)) {
			createMatch(fromUserId, toUserId);
		}
	}

	public List<Long> getLikedUsers(Long userId) {
		return interestRepository.findUserIdsByFromUserIdAndType(userId, InterestType.LIKE);
	}

	private void createMatch(Long user1Id, Long user2Id) {
		Match match = new Match();
		match.setUser1Id(user1Id);
		match.setUser2Id(user2Id);
		match.setCompatibilityScore(0.8); // Default score for mutual likes
		matchRepository.save(match);
	}
}