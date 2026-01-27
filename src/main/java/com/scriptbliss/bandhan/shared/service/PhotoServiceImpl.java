package com.scriptbliss.bandhan.shared.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scriptbliss.bandhan.shared.entity.Photo;
import com.scriptbliss.bandhan.shared.exception.ResourceNotFoundException;
import com.scriptbliss.bandhan.shared.repository.PhotoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

	private final PhotoRepository photoRepository;

	@Value("${app.upload.dir:uploads/photos}")
	private String uploadDir;

	@Override
	public List<Photo> getUserPhotos(Long userId) {
		return photoRepository.findByUserIdOrderBySortOrder(userId);
	}

	@Override
	public Photo uploadPhoto(Long userId, MultipartFile file, Boolean isPrimary) throws IOException {
		validateFile(file);

		// Handle primary photo logic
		if (isPrimary) {
			photoRepository.findByUserIdAndIsPrimaryTrue(userId).ifPresent(existingPrimary -> {
				existingPrimary.setIsPrimary(false);
				photoRepository.save(existingPrimary);
			});
		}

		// Create directory
		Path userDir = Paths.get(uploadDir, userId.toString());
		Files.createDirectories(userDir);

		// Generate secure filename
		String extension = getFileExtension(file.getContentType());
		String fileName = UUID.randomUUID().toString() + extension;
		Path filePath = userDir.resolve(fileName);
		Files.copy(file.getInputStream(), filePath);

		// Save to database with full URL path
		String urlPath = "/uploads/" + userId + "/" + fileName;
		Photo photo = new Photo();
		photo.setUserId(userId);
		photo.setFilePath(urlPath);
		photo.setFileSize(file.getSize());
		photo.setMimeType(file.getContentType());
		photo.setIsPrimary(isPrimary);
		return photoRepository.save(photo);
	}

	private String getFileExtension(String contentType) {
		switch (contentType) {
		case "image/jpeg":
			return ".jpg";
		case "image/png":
			return ".png";
		case "image/gif":
			return ".gif";
		case "image/webp":
			return ".webp";
		default:
			return ".jpg";
		}
	}

	private void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("Only image files are allowed");
		}
	}

	@Override
	public void deletePhoto(Long userId, Long photoId) {
		Photo photo = photoRepository.findById(photoId)
				.orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

		// Check ownership
		if (!photo.getUserId().equals(userId)) {
			throw new IllegalArgumentException("You can only delete your own photos");
		}

		// Delete file from disk
		String fileSystemPath = photo.getFilePath().replace("/uploads/", "");
		Path filePath = Paths.get(uploadDir, fileSystemPath);
		try {
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			// Log error but continue with database deletion
		}

		// Delete from database
		photoRepository.deleteById(photoId);
	}
}