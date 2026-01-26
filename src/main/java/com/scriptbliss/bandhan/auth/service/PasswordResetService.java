package com.scriptbliss.bandhan.auth.service;

public interface PasswordResetService {
	void requestPasswordReset(String email);

	String verifyResetToken(String token);

	void resetPasswordWithJWT(String jwt, String newPassword);
}