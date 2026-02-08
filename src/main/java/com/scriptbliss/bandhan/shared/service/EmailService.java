package com.scriptbliss.bandhan.shared.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final SesClient sesClient;

	@Value("${app.email.from:noreply@bandhan.scriptbliss.com}")
	private String fromEmail;

	@Value("${app.frontend.url:http://localhost:5173}")
	private String frontendUrl;

	public void sendVerificationEmail(String toEmail, String token) {
		try {
			SendEmailRequest request = SendEmailRequest.builder().source(fromEmail)
					.destination(Destination.builder().toAddresses(toEmail).build())
					.message(Message.builder().subject(Content.builder().data("Verify your Matrimony account").build())
							.body(Body.builder()
									.text(Content.builder().data(buildVerificationEmailContent(token)).build()).build())
							.build())
					.build();

			sesClient.sendEmail(request);
			log.info("Verification email sent to: {}", toEmail);
		} catch (Exception e) {
			log.error("Failed to send verification email to: {}", toEmail, e);
			throw new RuntimeException("Failed to send verification email", e);
		}
	}

	public void sendPasswordResetEmail(String toEmail, String token) {
		try {
			SendEmailRequest request = SendEmailRequest.builder().source(fromEmail)
					.destination(Destination.builder().toAddresses(toEmail).build())
					.message(Message.builder().subject(Content.builder().data("Reset your password").build())
							.body(Body.builder()
									.text(Content.builder().data(buildPasswordResetEmailContent(token)).build())
									.build())
							.build())
					.build();

			sesClient.sendEmail(request);
			log.info("Password reset email sent to: {}", toEmail);
		} catch (Exception e) {
			log.error("Failed to send password reset email to: {}", toEmail, e);
			throw new RuntimeException("Failed to send password reset email", e);
		}
	}

	private String buildVerificationEmailContent(String token) {
		return String.format(
				"Welcome to Matrimony Portal!\n\n" + "Please click the link below to verify your email address:\n"
						+ "%s/register/verify?token=%s\n\n" + "This link will expire in 24 hours.\n\n"
						+ "If you didn't create an account, please ignore this email.\n\n" + "Best regards,\n"
						+ "Matrimony Portal Team",
				frontendUrl, token);
	}

	private String buildPasswordResetEmailContent(String token) {
		return String.format("Password Reset Request\n\n" + "Click the link below to reset your password:\n"
				+ "%s/reset-password?token=%s\n\n" + "This link will expire in 1 hour.\n\n"
				+ "If you didn't request a password reset, please ignore this email.\n\n" + "Best regards,\n"
				+ "Matrimony Portal Team", frontendUrl, token);
	}
}