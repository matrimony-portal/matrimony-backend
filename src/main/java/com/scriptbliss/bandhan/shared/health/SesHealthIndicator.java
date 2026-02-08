package com.scriptbliss.bandhan.shared.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.GetAccountSendingEnabledRequest;

@Component
@RequiredArgsConstructor
public class SesHealthIndicator implements HealthIndicator {

	private final SesClient sesClient;

	@Override
	public Health health() {
		try {
			sesClient.getAccountSendingEnabled(GetAccountSendingEnabledRequest.builder().build());
			return Health.up().withDetail("service", "AWS SES").build();
		} catch (Exception e) {
			return Health.down().withDetail("service", "AWS SES").withDetail("error", e.getMessage()).build();
		}
	}
}
