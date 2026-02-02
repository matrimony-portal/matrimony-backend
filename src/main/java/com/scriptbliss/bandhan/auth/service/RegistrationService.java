package com.scriptbliss.bandhan.auth.service;

import com.scriptbliss.bandhan.auth.dto.request.CompleteRegistrationRequest;
import com.scriptbliss.bandhan.auth.dto.request.RegisterRequest;
import com.scriptbliss.bandhan.auth.dto.response.RegisterResponse;

public interface RegistrationService {
	void startRegistration(String email);

	String verifyEmailForRegistration(String token);

	void completeRegistration(String jwt, CompleteRegistrationRequest request);

	RegisterResponse register(RegisterRequest request);
}
