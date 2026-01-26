package com.scriptbliss.bandhan.auth.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.scriptbliss.bandhan.auth.dto.request.LoginRequest;
import com.scriptbliss.bandhan.auth.dto.response.AuthResponse;
import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.AccountStatus;
import com.scriptbliss.bandhan.auth.enums.JwtScope;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.shared.exception.BusinessException;
import com.scriptbliss.bandhan.shared.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public AuthResponse login(LoginRequest request) {
		Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

		if (userOpt.isEmpty()) {
			throw new BusinessException("INVALID_CREDENTIALS", "Invalid email or password");
		}

		User user = userOpt.get();

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException("INVALID_CREDENTIALS", "Invalid email or password");
		}

		if (user.getStatus() == AccountStatus.INACTIVE) {
			throw new BusinessException("ACCOUNT_INACTIVE", "Account is not active. Please contact support.");
		}

		if (user.getStatus() == AccountStatus.BLOCKED) {
			throw new BusinessException("ACCOUNT_BLOCKED", "Account has been blocked. Please contact support.");
		}

		String accessToken = jwtUtil.generateAccessToken(user);
		String refreshToken = jwtUtil.generateRefreshToken(user);

		log.info("User logged in: {}", user.getEmail());

		return AuthResponse.builder().token(accessToken).refreshToken(refreshToken).expiresIn(24 * 60 * 60) // 24 hours
				.user(AuthResponse.UserInfo.builder().id(user.getId()).email(user.getEmail())
						.firstName(user.getFirstName()).lastName(user.getLastName()).role(user.getRole()).build())
				.build();
	}

	@Override
	public void logout(String refreshToken) {
		try {
			Claims claims = jwtUtil.validateToken(refreshToken, JwtScope.REFRESH);
			Long userId = claims.get("userId", Long.class);
			log.info("User logged out: userId={}", userId);
		} catch (Exception e) {
			log.warn("Invalid refresh token during logout: {}", e.getMessage());
			// Don't throw exception - logout should always succeed
		}
	}

	@Override
	public AuthResponse refreshToken(String refreshToken) {
		Claims claims = jwtUtil.validateToken(refreshToken, JwtScope.REFRESH);

		Long userId = claims.get("userId", Long.class);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Invalid or expired token"));

		if (user.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException("INVALID_TOKEN", "Invalid or expired token");
		}

		String newAccessToken = jwtUtil.generateAccessToken(user);
		String newRefreshToken = jwtUtil.generateRefreshToken(user);

		log.info("Token refreshed for user: {}", user.getEmail());

		return AuthResponse.builder().token(newAccessToken).refreshToken(newRefreshToken).expiresIn(24 * 60 * 60)
				.user(AuthResponse.UserInfo.builder().id(user.getId()).email(user.getEmail())
						.firstName(user.getFirstName()).lastName(user.getLastName()).role(user.getRole()).build())
				.build();
	}
}