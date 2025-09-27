package com.spring.human.resource.server.configs.language;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceHelper {
    //  Khai báo bean để Spring quản lý MessageSource
    @Bean
    MessageSource messageSource() {
        // Chỉ định file messages.properties trong classpath để lấy đa ngôn ngữ
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        // Đặt encoding UTF-8 để đọc tiếng Việt, tiếng nước ngoài
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    // Lấy message theo code, tham số args và Locale hiện tại trong LanguageContext
    public String getMessage(String code, Object... args) {
        return messageSource().getMessage(code, args, LanguageContext.getLocale());
    }
}
