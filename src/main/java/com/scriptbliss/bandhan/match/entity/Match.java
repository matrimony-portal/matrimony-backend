package com.scriptbliss.bandhan.match.entity;

import com.scriptbliss.bandhan.shared.entity.BaseEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "matches")
@AttributeOverride(name = "id", column = @Column(name = "match_id"))
@Data
@EqualsAndHashCode(callSuper = true)
public class Match extends BaseEntity {

	@Column(name = "user1_id", nullable = false)
	private Long user1Id;

	@Column(name = "user2_id", nullable = false)
	private Long user2Id;

	@Column(name = "compatibility_score")
	private Double compatibilityScore;
}