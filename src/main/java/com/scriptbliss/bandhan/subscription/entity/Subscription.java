package com.scriptbliss.bandhan.subscription.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.subscription.enums.BillingCycle;
import com.scriptbliss.bandhan.shared.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends BaseEntity {

	@Column(name = "user_id", unique = true, nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id", nullable = false)
	private SubscriptionPlan plan;

	@Enumerated(EnumType.STRING)
	@Column(name = "billing_cycle", length = 10)
	private BillingCycle billingCycle;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "auto_renew")
	private Boolean autoRenew;

	@Column(name = "payment_amount", precision = 10, scale = 2)
	private BigDecimal paymentAmount;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Column(name = "next_billing_date")
	private LocalDate nextBillingDate;
}
