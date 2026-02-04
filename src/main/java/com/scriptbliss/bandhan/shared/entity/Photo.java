package com.scriptbliss.bandhan.shared.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scriptbliss.bandhan.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "file_name", nullable = false, length = 255)
	private String fileName;

	@Column(name = "file_path", nullable = false, length = 500)
	private String filePath;

	@Column(name = "file_size", nullable = false)
	private Long fileSize;

	@Column(name = "mime_type", nullable = false, length = 100)
	private String mimeType;

	@Column(name = "is_primary")
	private Boolean isPrimary = false;

	@Column(name = "sort_order")
	private Integer sortOrder = 0;

	@Column(name = "alt_text")
	private String altText;

	@Column(name = "uploaded_at")
	private LocalDateTime uploadedAt = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	private User user;
}