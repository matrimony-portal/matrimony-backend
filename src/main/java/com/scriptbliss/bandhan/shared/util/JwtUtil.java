package com.scriptbliss.bandhan.shared.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.scriptbliss.bandhan.auth.enums.JwtScope;
import com.scriptbliss.bandhan.shared.exception.BusinessException;
import com.scriptbliss.bandhan.shared.security.CustomUserDetailsService.CustomUserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for JWT token operations including generation, validation, and
 * parsing. Handles different token types: access, refresh, registration, and
 * password reset tokens.
 */
@Component
@Slf4j
public class JwtUtil {

	/** JWT signing secret key */
	private final SecretKey secretKey;

	/**
	 * Constructor to initialize JWT utility with secret key.
	 * 
	 * @param jwtSecret Secret key for JWT signing from application properties
	 */
	public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	/**
	 * Generates access token from user principal with 24-hour expiration.
	 * 
	 * @param principal User principal containing user details
	 * @return JWT access token string
	 */
	public String generateAccessToken(CustomUserPrincipal principal) {
		return Jwts.builder().claim("userId", principal.getUserId()).claim("email", principal.getEmail())
				.claim("role", principal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
				.claim("scope", JwtScope.ACCESS.name()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
				.signWith(secretKey).compact();
	}

	/**
	 * Generates refresh token from user principal with 7-day expiration.
	 * 
	 * @param principal User principal containing user details
	 * @return JWT refresh token string
	 */
	public String generateRefreshToken(CustomUserPrincipal principal) {
		return Jwts.builder().claim("userId", principal.getUserId()).claim("scope", JwtScope.REFRESH.name())
				.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7
																													// days

				.signWith(secretKey).compact();
	}

	/**
	 * Generates registration token for email verification with 30-minute
	 * expiration.
	 * 
	 * @param email User email address
	 * @return JWT registration token string
	 */
	public String generateRegistrationToken(String email) {
		return Jwts.builder().claim("email", email).claim("scope", JwtScope.REGISTRATION.name()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 minutes
				.signWith(secretKey).compact();
	}

	/**
	 * Generates password reset token with 15-minute expiration.
	 * 
	 * @param email User email address
	 * @return JWT password reset token string
	 */
	public String generatePasswordResetToken(String email) {
		return Jwts.builder().claim("email", email).claim("scope", JwtScope.PASSWORD_RESET.name()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
				.signWith(secretKey).compact();
	}

	/**
	 * Validates JWT token and verifies scope matches expected value.
	 * 
	 * @param jwt           JWT token string to validate
	 * @param expectedScope Expected token scope for validation
	 * @return Claims object containing token payload
	 * @throws BusinessException if token is invalid or scope mismatch
	 */
	public Claims validateToken(String jwt, JwtScope expectedScope) {
		try {
			// Parse and verify token signature
			Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();

			// Verify token scope matches expected
			String scope = claims.get("scope", String.class);
			if (!expectedScope.name().equals(scope)) {
				throw new BusinessException("INVALID_TOKEN_SCOPE", "Invalid token scope");
			}

			return claims;
		} catch (Exception e) {
			log.warn("JWT validation failed: {}", e.getMessage());
			throw new BusinessException("INVALID_TOKEN", "Invalid or expired token");
		}
	}

	/**
	 * Parses JWT token without scope validation.
	 * 
	 * @param jwt JWT token string to parse
	 * @return Claims object containing token payload
	 * @throws BusinessException if token is invalid or expired
	 */
	public Claims parseToken(String jwt) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();
		} catch (Exception e) {
			log.warn("JWT parsing failed: {}", e.getMessage());
			throw new BusinessException("INVALID_TOKEN", "Invalid or expired token");
		}
	}
}