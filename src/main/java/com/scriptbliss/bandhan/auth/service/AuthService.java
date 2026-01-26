package com.scriptbliss.bandhan.auth.service;

import com.scriptbliss.bandhan.auth.dto.request.LoginRequest;
import com.scriptbliss.bandhan.auth.dto.response.AuthResponse;

public interface AuthService {
	AuthResponse login(LoginRequest request);
	void logout(String refreshToken);
	AuthResponse refreshToken(String refreshToken);
}