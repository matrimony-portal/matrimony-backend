package com.scriptbliss.bandhan.subscription.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scriptbliss.bandhan.subscription.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	Optional<Subscription> findByUserId(Long userId);

	@Query("SELECT s FROM Subscription s JOIN FETCH s.plan WHERE s.userId = :userId AND (s.isActive = true OR s.isActive IS NULL)")
	Optional<Subscription> findActiveByUserIdWithPlan(Long userId);
}
