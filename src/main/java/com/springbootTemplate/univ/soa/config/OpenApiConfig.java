package com.springbootTemplate.univ.soa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI feedbackServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservice Feedback API")
                        .description("API REST pour la gestion des feedbacks utilisateurs")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nassim")
                                .email("nassim@smartdish.com")));
    }
}