
package com.scriptbliss.bandhan.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
	@GetMapping
	public String getMethodName() {
		return new String("Ok");
	}

	@GetMapping("/health")
	public String health() {
		return "OK";
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello from Matrimony Backend!";
	}
}
