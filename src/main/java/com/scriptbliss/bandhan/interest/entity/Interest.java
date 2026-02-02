package com.scriptbliss.bandhan.interest.entity;

import java.math.BigDecimal;

import com.scriptbliss.bandhan.interest.enums.InterestType;
import com.scriptbliss.bandhan.shared.entity.BaseEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_interests")
@AttributeOverride(name = "id", column = @Column(name = "id"))
@AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", insertable = false, updatable = false))
@Data
@EqualsAndHashCode(callSuper = true)
public class Interest extends BaseEntity {

	@Column(name = "from_user_id", nullable = false)
	private Long fromUserId;

	@Column(name = "to_user_id", nullable = false)
	private Long toUserId;

	@Enumerated(EnumType.STRING)
	@Column(name = "interest_type", nullable = false)
	private InterestType type;

	@Column(name = "compatibility_score", precision = 5, scale = 2)
	private BigDecimal compatibilityScore;

	@Column(columnDefinition = "TEXT")
	private String notes;
}