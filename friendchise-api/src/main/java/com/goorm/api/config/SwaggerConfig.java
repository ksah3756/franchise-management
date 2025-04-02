package com.goorm.api.config;

import com.goorm.api.auth.dto.request.LoginRequest;
import com.goorm.api.auth.filter.LoginFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SwaggerConfig {
	private final ApplicationContext applicationContext;
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

	@Bean
	public OpenApiCustomizer loginEndpointCustomizer() {
		FilterChainProxy filterChainProxy = applicationContext.getBean(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
		return openAPI -> {
			for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
				Optional<LoginFilter> optionalFilter =
						filterChain.getFilters().stream()
								.filter(LoginFilter.class::isInstance)
								.map(LoginFilter.class::cast)
								.findAny();

				if (optionalFilter.isPresent()) {
					LoginFilter loginFilter = optionalFilter.get();
					Operation operation = new Operation();
					Schema<?> schema = new ObjectSchema()
							.addProperty(loginFilter.getUsernameParameter(), new StringSchema())
							.addProperty(loginFilter.getPasswordParameter(), new StringSchema());
					String mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

					RequestBody requestBody = new RequestBody().content(new Content().addMediaType(mediaType, new io.swagger.v3.oas.models.media.MediaType().schema(schema)));
					operation.requestBody(requestBody);
					ApiResponses apiResponses = new ApiResponses();
					apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
					apiResponses.addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), new ApiResponse().description(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
					operation.responses(apiResponses);
					operation.addTagsItem("login-endpoint");
					PathItem pathItem = new PathItem().post(operation);
					try {
						AntPathRequestMatcher requestMatcher = (AntPathRequestMatcher) FieldUtils.readField(loginFilter, "requiresAuthenticationRequestMatcher", true);
						String loginPath = requestMatcher.getPattern();
						openAPI.getPaths().addPathItem(loginPath, pathItem);
					} catch (IllegalAccessException |
							 ClassCastException ignored) {
						// Exception escaped
						log.trace(ignored.getMessage());
					}
				}
			}
		};
	}
}
