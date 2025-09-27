package com.spring.human.resource.server.configs.language;

import com.spring.human.lib.enumerated.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DetectLanguage {
    Language value() default Language.EN;
}
