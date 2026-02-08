package com.scriptbliss.bandhan.interest.entity;

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
@Table(name = "interests")
@AttributeOverride(name = "id", column = @Column(name = "interest_id"))
@Data
@EqualsAndHashCode(callSuper = true)
public class Interest extends BaseEntity {

	private Long fromUserId;
	private Long toUserId;

	@Enumerated(EnumType.STRING)
	private InterestType type;
}