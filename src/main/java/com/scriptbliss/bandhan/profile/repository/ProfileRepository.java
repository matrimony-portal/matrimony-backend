package com.scriptbliss.bandhan.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scriptbliss.bandhan.profile.entity.Profile;
import com.scriptbliss.bandhan.profile.enums.Gender;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
	Optional<Profile> findByUserId(Long userId);

	@Query("SELECT p FROM Profile p WHERE p.gender = ?1 AND YEAR(CURRENT_DATE) - YEAR(p.dateOfBirth) BETWEEN ?2 AND ?3 AND p.user.id != ?4 AND p.user.id NOT IN (SELECT i.toUserId FROM Interest i WHERE i.fromUserId = ?4)")
	List<Profile> findPotentialMatches(Gender gender, int minAge, int maxAge, Long excludeUserId, Pageable pageable);
}