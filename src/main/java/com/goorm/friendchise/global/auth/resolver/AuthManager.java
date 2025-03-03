package com.goorm.friendchise.global.auth.resolver;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthManager {
    boolean errorOnInvalidType() default true;
}
