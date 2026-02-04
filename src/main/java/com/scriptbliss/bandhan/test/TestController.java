
package com.scriptbliss.bandhan.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

	private final BCryptPasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@GetMapping
	public String getMethodName() {
		return new String("OK");
	}

	@GetMapping("/health")
	public String health() {
		return "OK";
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello from Matrimony Backend!";
	}

	@GetMapping("/check-password")
	public String checkPassword(@RequestParam String email, @RequestParam String password) {
		return userRepository.findByEmail(email)
			.map(user -> {
				boolean matches = passwordEncoder.matches(password, user.getPassword());
				return "User: " + user.getEmail() + 
					   ", Status: " + user.getStatus() + 
					   ", Password matches: " + matches +
					   ", Hash: " + user.getPassword().substring(0, 20) + "...";
			})
			.orElse("User not found: " + email);
	}

	@GetMapping("/generate-hash")
	public String generateHash(@RequestParam String password) {
		return passwordEncoder.encode(password);
	}
}
