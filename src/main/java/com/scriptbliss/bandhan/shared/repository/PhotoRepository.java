package com.scriptbliss.bandhan.shared.repository;

import com.scriptbliss.bandhan.shared.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

	List<Photo> findByUserIdOrderBySortOrder(Long userId);

	Optional<Photo> findByUserIdAndIsPrimaryTrue(Long userId);

	@Query("SELECT p FROM Photo p WHERE p.userId = :userId AND p.isPrimary = false ORDER BY p.sortOrder")
	List<Photo> findGalleryPhotosByUserId(Long userId);

	void deleteByUserIdAndId(Long userId, Long photoId);

	long countByUserId(Long userId);
}