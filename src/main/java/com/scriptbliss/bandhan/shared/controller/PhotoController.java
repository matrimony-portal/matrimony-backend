package com.scriptbliss.bandhan.shared.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scriptbliss.bandhan.shared.entity.Photo;
import com.scriptbliss.bandhan.shared.security.CustomUserDetailsService.CustomUserPrincipal;
import com.scriptbliss.bandhan.shared.service.PhotoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
public class PhotoController {

	private final PhotoService photoService;

	@GetMapping("/my-photos")
	public List<Photo> getMyPhotos(@AuthenticationPrincipal CustomUserPrincipal principal) {
		return photoService.getUserPhotos(principal.getUserId());
	}

	@GetMapping("/user/{userId}")
	public List<Photo> getUserPhotos(@PathVariable Long userId) {
		return photoService.getUserPhotos(userId);
	}

	@PostMapping("/upload")
	public Photo uploadPhoto(@AuthenticationPrincipal CustomUserPrincipal principal, @RequestParam MultipartFile file,
			@RequestParam(defaultValue = "false") Boolean isPrimary) throws IOException {
		return photoService.uploadPhoto(principal.getUserId(), file, isPrimary);
	}

	@DeleteMapping("/{photoId}")
	public ResponseEntity<Void> deletePhoto(@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long photoId) {
		photoService.deletePhoto(principal.getUserId(), photoId);
		return ResponseEntity.ok().build();
	}
}