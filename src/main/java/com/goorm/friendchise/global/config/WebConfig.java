package com.goorm.friendchise.global.config;

import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.global.auth.resolver.AuthManagerArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ManagerRepository managerRepository;

    public WebConfig(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthManagerArgumentResolver(managerRepository));
    }
}
