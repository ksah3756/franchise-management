package com.goorm.friendchise.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(apiInfo())
			.addSecurityItem(securityRequirement())
			.components(components());
	}

	private SecurityRequirement securityRequirement() {
		return new SecurityRequirement().addList("bearer");
	}

	private Info apiInfo() {
		return new Info()
			.title("Friendchise");
	}

	private Components components() {
		return new Components().addSecuritySchemes("bearer", securityScheme());
	}

	private SecurityScheme securityScheme() {
		return new SecurityScheme()
			.name("bearer")
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");
	}
}
