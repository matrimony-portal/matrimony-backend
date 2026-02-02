package com.scriptbliss.bandhan.subscription.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scriptbliss.bandhan.subscription.entity.SubscriptionPlan;
import com.scriptbliss.bandhan.subscription.enums.PlanType;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

	Optional<SubscriptionPlan> findByPlanType(PlanType planType);
}
