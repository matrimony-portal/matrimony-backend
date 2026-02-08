package com.scriptbliss.bandhan.match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scriptbliss.bandhan.match.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
	
	@Query("SELECT m FROM Match m WHERE m.user1Id = ?1 OR m.user2Id = ?1 ORDER BY m.compatibilityScore DESC")
	List<Match> findByUserId(Long userId);
}