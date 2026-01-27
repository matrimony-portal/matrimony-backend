package com.scriptbliss.bandhan.interest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scriptbliss.bandhan.interest.entity.Interest;
import com.scriptbliss.bandhan.interest.enums.InterestType;

public interface InterestRepository extends JpaRepository<Interest, Long> {
	boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	@Query("SELECT i.toUserId FROM Interest i WHERE i.fromUserId = ?1 AND i.type = ?2")
	List<Long> findUserIdsByFromUserIdAndType(Long fromUserId, InterestType type);

	@Query("SELECT i FROM Interest i WHERE i.fromUserId = ?1 AND i.toUserId IN (SELECT i2.fromUserId FROM Interest i2 WHERE i2.toUserId = ?1 AND i2.type = 'LIKE') AND i.type = 'LIKE'")
	List<Interest> findMutualLikes(Long userId);
}