package com.scriptbliss.bandhan.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class PasswordResetServiceImpl implements PasswordResetService {

	private final UserRepository userRepository;
	private final VerificationTokenRepository tokenRepository;
	private final EmailService emailService;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public void requestPasswordReset(String email) {
		Optional<User> userOpt = userRepository.findByEmail(email);

		if (userOpt.isEmpty() || userOpt.get().getStatus() != AccountStatus.ACTIVE) {
			// Security: Don't reveal if user exists
			log.warn("Password reset requested for: {}", email);
			return;
		}

		String token = createPasswordResetToken(userOpt.get());
		emailService.sendPasswordResetEmail(email, token);
		log.info("Password reset email sent to: {}", email);
	}

	@Override
	public String verifyResetToken(String tokenValue) {
		Optional<VerificationToken> tokenOpt = tokenRepository.findByTokenAndTokenTypeAndUsedAtIsNull(tokenValue,
				TokenType.PASSWORD_RESET);

		if (tokenOpt.isEmpty()) {
			throw new BusinessException("INVALID_TOKEN", "Invalid or expired reset token");
		}

		VerificationToken token = tokenOpt.get();
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException("TOKEN_EXPIRED", "Reset token has expired");
		}

		// Mark token as used
		token.setUsed(true);
		token.setUsedAt(LocalDateTime.now());
		tokenRepository.save(token);

		String email = token.getUser().getEmail();
		log.info("Reset token verified for: {}", email);

		return jwtUtil.generatePasswordResetToken(email);
	}

	@Override
	public void resetPasswordWithJWT(String jwt, String newPassword) {
		Claims claims = jwtUtil.validateToken(jwt, JwtScope.PASSWORD_RESET);
		String email = claims.get("email", String.class);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		log.info("Password reset completed for user: {}", email);
	}

	private String createPasswordResetToken(User user) {
		String tokenValue = UUID.randomUUID().toString();
		VerificationToken token = VerificationToken.builder().token(tokenValue).email(user.getEmail()).user(user)
				.tokenType(TokenType.PASSWORD_RESET).expiresAt(LocalDateTime.now().plusMinutes(30)).build();

		tokenRepository.save(token);
		return tokenValue;
	}

}