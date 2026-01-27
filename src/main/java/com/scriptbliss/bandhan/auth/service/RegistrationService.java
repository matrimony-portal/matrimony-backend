package com.scriptbliss.bandhan.auth.service;

import com.scriptbliss.bandhan.auth.dto.request.CompleteRegistrationRequest;

public interface RegistrationService {
	void startRegistration(String email);

	String verifyEmailForRegistration(String token);

	void completeRegistration(String jwt, CompleteRegistrationRequest request);
}
