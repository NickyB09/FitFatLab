package com.fitfatlab.fitfatlab_backend.security;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "@userResolver.resolve(#this)")
public @interface CurrentUser {}
