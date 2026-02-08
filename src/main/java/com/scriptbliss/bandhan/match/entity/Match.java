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

	private Long user1Id;
	private Long user2Id;
	private Double compatibilityScore;
}