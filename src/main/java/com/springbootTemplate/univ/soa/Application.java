package com.springbootTemplate.univ.soa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.println("\n" +
				"========================================\n" +
				"✅ Microservice Feedback démarré!\n" +
				"========================================\n" +
				"📍 API: http://localhost:8090/api/feedbacks\n" +
				"📊 Swagger: http://localhost:8090/swagger-ui.html\n" +
				"💚 Health: http://localhost:8090/api/feedbacks/health\n" +
				"========================================\n");
	}
}