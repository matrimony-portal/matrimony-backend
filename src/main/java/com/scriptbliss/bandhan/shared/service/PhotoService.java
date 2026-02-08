package com.scriptbliss.bandhan.shared.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scriptbliss.bandhan.shared.entity.Photo;

public interface PhotoService {

	List<Photo> getUserPhotos(Long userId);

	Photo uploadPhoto(Long userId, MultipartFile file, Boolean isPrimary) throws IOException;

	void deletePhoto(Long userId, Long photoId);
}