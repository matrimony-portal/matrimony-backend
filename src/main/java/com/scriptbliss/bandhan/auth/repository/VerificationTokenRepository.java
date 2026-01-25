package com.scriptbliss.bandhan.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scriptbliss.bandhan.auth.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Optional<VerificationToken> findByTokenAndIsUsedFalse(String token);

}