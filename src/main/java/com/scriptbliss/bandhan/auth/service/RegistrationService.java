package com.scriptbliss.bandhan.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scriptbliss.bandhan.auth.dto.request.RegisterRequest;
import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.entity.VerificationToken;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.auth.repository.VerificationTokenRepository;
import com.scriptbliss.bandhan.shared.exception.BusinessException;
import com.scriptbliss.bandhan.shared.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegistrationService {

	private final UserRepository userRepository;
	private final VerificationTokenRepository tokenRepository;
	private final EmailService emailService;
	private final BCryptPasswordEncoder passwordEncoder;

	/**
	 * Registers a new user or updates existing unverified user. Allows
	 * re-registration for unverified accounts to update details.
	 */
	public void registerUser(RegisterRequest request) {
		Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

		if (existingUser.isPresent()) {
			User user = existingUser.get();
			if (user.isActive()) {
				throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email already registered and verified");
			}
			// Allow re-registration for unverified users
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			user.setFirstName(request.getFirstName());
			user.setLastName(request.getLastName());
			user.setPhone(request.getPhone());
			user.setRole(request.getRole());
			User savedUser = userRepository.save(user);
			log.info("Updated existing unverified user: {}", request.getEmail());

			String token = createVerificationToken(savedUser);
			emailService.sendVerificationEmail(request.getEmail(), token);
			return;
		}

		User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName()).lastName(request.getLastName()).phone(request.getPhone())
				.role(request.getRole()).build();

		User savedUser = userRepository.save(user);
		log.info("User registered with email: {}", request.getEmail());

		String token = createVerificationToken(savedUser);
		emailService.sendVerificationEmail(request.getEmail(), token);
	}

	/**
	 * Resends verification email for unverified users. Uses silent fail to prevent
	 * user enumeration attacks.
	 */
	public void resendVerificationEmail(String email) {
		Optional<User> existingUser = userRepository.findByEmail(email);

		if (existingUser.isEmpty() || existingUser.get().isActive()) {
			// Security: Don't reveal if user exists or verification status
			log.warn("Resend verification attempted for: {}", email);
			return;
		}

		String token = createVerificationToken(existingUser.get());
		emailService.sendVerificationEmail(email, token);
		log.info("Verification email resent to: {}", email);
	}

	/**
	 * Creates verification token with 30-minute expiry. Keeps audit trail by not
	 * deleting old tokens.
	 */
	private String createVerificationToken(User user) {
		String tokenValue = UUID.randomUUID().toString();
		VerificationToken token = VerificationToken.builder().token(tokenValue).user(user)
				.expiresAt(LocalDateTime.now().plusMinutes(30)).build();

		tokenRepository.save(token);
		return tokenValue;
	}

	public void verifyEmail(String tokenValue) {
		Optional<VerificationToken> tokenOpt = tokenRepository.findByTokenAndIsUsedFalse(tokenValue);

		if (tokenOpt.isEmpty()) {
			throw new BusinessException("INVALID_TOKEN", "Invalid or expired verification token");
		}

		VerificationToken token = tokenOpt.get();
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException("TOKEN_EXPIRED", "Verification token has expired");
		}

		// Mark token as used
		token.setUsed(true);
		token.setUsedAt(LocalDateTime.now());
		tokenRepository.save(token);

		// Activate user
		User user = token.getUser();
		user.setActive(true);
		userRepository.save(user);

		log.info("Email verified for user: {}", user.getEmail());
	}
}
