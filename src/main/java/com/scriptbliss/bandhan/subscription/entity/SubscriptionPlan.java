package com.scriptbliss.bandhan.subscription.entity;

import java.math.BigDecimal;

import com.scriptbliss.bandhan.subscription.enums.CustomerSupportLevel;
import com.scriptbliss.bandhan.subscription.enums.PlanType;
import com.scriptbliss.bandhan.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends BaseEntity {

	@Column(name = "plan_name", unique = true, nullable = false, length = 50)
	private String planName;

	@Enumerated(EnumType.STRING)
	@Column(name = "plan_type", unique = true, nullable = false, length = 10)
	private PlanType planType;

	@Column(name = "price_monthly", precision = 10, scale = 2)
	private BigDecimal priceMonthly;

	@Column(name = "price_yearly", precision = 10, scale = 2)
	private BigDecimal priceYearly;

	@Column(name = "max_profiles_view")
	private Integer maxProfilesView;

	@Column(name = "max_messages")
	private Integer maxMessages;

	@Column(name = "max_photos")
	private Integer maxPhotos;

	@Column(name = "priority_matching")
	private Boolean priorityMatching;

	@Column(name = "advanced_filters")
	private Boolean advancedFilters;

	@Enumerated(EnumType.STRING)
	@Column(name = "customer_support", length = 10)
	private CustomerSupportLevel customerSupport;

	@Column(name = "is_active")
	private Boolean isActive;
}
