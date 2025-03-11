package com.goorm.friendchise.global.auth.resolver;

import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.exception.CustomerException;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class AuthCustomerArgumentResolver implements HandlerMethodArgumentResolver {
    private final CustomerRepository customerRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentManager 애노테이션이 붙어 있고, 파라미터 타입이 Manager여야 함.
        return parameter.getParameterAnnotation(AuthManager.class) != null
                && Manager.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            throw new CustomerException();
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return customerRepository.findByUsername(username)
                .orElseThrow(CustomerException::new);
    }
}
