package com.scriptbliss.bandhan.shared.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;

	@Value("${app.email.from:noreply@bandhan.scriptbliss.com}")
	private String fromEmail;

	public void sendVerificationEmail(String toEmail, String token) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toEmail);
			message.setSubject("Verify your Matrimony account");
			message.setText(buildVerificationEmailContent(token));
			message.setFrom(fromEmail); // Verified domain identity

			mailSender.send(message);
			log.info("Verification email sent to: {}", toEmail);
		} catch (Exception e) {
			log.error("Failed to send verification email to: {}", toEmail, e);
			throw new RuntimeException("Failed to send verification email", e);
		}
	}

	public void sendPasswordResetEmail(String toEmail, String token) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toEmail);
			message.setSubject("Reset your password");
			message.setText(buildPasswordResetEmailContent(token));
			message.setFrom(fromEmail); // Verified domain identity

			mailSender.send(message);
			log.info("Password reset email sent to: {}", toEmail);
		} catch (Exception e) {
			log.error("Failed to send password reset email to: {}", toEmail, e);
			throw new RuntimeException("Failed to send password reset email", e);
		}
	}

	private String buildVerificationEmailContent(String token) {
		return String.format("Welcome to Matrimony Portal!\n\n"
				+ "Please click the link below to verify your email address:\n"
				+ "http://localhost:8080/register/verify-email?token=%s\n\n" + "This link will expire in 24 hours.\n\n"
				+ "If you didn't create an account, please ignore this email.\n\n" + "Best regards,\n"
				+ "Matrimony Portal Team", token);
	}

	private String buildPasswordResetEmailContent(String token) {
		return String.format("Password Reset Request\n\n" + "Click the link below to reset your password:\n"
				+ "http://localhost:8080/auth/reset-password?token=%s\n\n" + "This link will expire in 1 hour.\n\n"
				+ "If you didn't request a password reset, please ignore this email.\n\n" + "Best regards,\n"
				+ "Matrimony Portal Team", token);
	}
}