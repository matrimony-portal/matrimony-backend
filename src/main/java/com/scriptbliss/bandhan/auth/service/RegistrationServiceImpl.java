package com.scriptbliss.bandhan.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scriptbliss.bandhan.auth.dto.request.CompleteRegistrationRequest;
import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.entity.VerificationToken;
import com.scriptbliss.bandhan.auth.enums.AccountStatus;
import com.scriptbliss.bandhan.auth.enums.JwtScope;
import com.scriptbliss.bandhan.auth.enums.TokenType;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.auth.repository.VerificationTokenRepository;
import com.scriptbliss.bandhan.shared.exception.BusinessException;
import com.scriptbliss.bandhan.shared.service.EmailService;
import com.scriptbliss.bandhan.shared.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

	private final UserRepository userRepository;
	private final VerificationTokenRepository tokenRepository;
	private final EmailService emailService;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public void startRegistration(String email) {
		Optional<User> existingUser = userRepository.findByEmail(email);

		if (existingUser.isPresent()) {
			throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email already registered");
		}

		String token = createVerificationToken(email);
		emailService.sendVerificationEmail(email, token);
		log.info("Registration started for email: {}", email);
	}

	@Override
	public String verifyEmailForRegistration(String tokenValue) {
		Optional<VerificationToken> tokenOpt = tokenRepository.findByTokenAndTokenTypeAndIsUsedFalse(tokenValue,
				TokenType.EMAIL_VERIFICATION);

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

		String email = token.getEmail();
		log.info("Email verified for registration: {}", email);

		return jwtUtil.generateRegistrationToken(email);
	}

	@Override
	public void completeRegistration(String jwt, CompleteRegistrationRequest request) {
		Claims claims = jwtUtil.validateToken(jwt, JwtScope.REGISTRATION);
		String email = claims.get("email", String.class);

		Optional<User> existingUser = userRepository.findByEmail(email);
		if (existingUser.isPresent()) {
			throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email already registered");
		}

		User user = User.builder().email(email).password(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName()).lastName(request.getLastName()).phone(request.getPhone())
				.role(request.getRole()).status(AccountStatus.ACTIVE).build();

		userRepository.save(user);
		log.info("Registration completed for user: {}", email);
	}

	private String createVerificationToken(String email) {
		String tokenValue = UUID.randomUUID().toString();
		VerificationToken token = VerificationToken.builder().token(tokenValue).email(email)
				.tokenType(TokenType.EMAIL_VERIFICATION).expiresAt(LocalDateTime.now().plusMinutes(30)).build();

		tokenRepository.save(token);
		return tokenValue;
	}

}