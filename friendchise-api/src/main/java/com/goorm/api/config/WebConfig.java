package com.goorm.api.config;

import com.goorm.api.auth.resolver.AuthUserArgumentResolver;
import com.goorm.api.user.implement.UserReader;
import com.goorm.core.user.domain.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserReader userReader;

    public WebConfig(UserReader userReader) {
        this.userReader = userReader;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthUserArgumentResolver(userReader));
    }
}
