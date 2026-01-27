package com.scriptbliss.bandhan.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scriptbliss.bandhan.auth.entity.VerificationToken;
import com.scriptbliss.bandhan.auth.enums.TokenType;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Optional<VerificationToken> findByTokenAndUsedAtIsNull(String token);

	Optional<VerificationToken> findByTokenAndTokenTypeAndUsedAtIsNull(String token, TokenType tokenType);

}