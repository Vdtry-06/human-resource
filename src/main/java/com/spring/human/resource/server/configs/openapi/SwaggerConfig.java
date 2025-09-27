package com.spring.human.resource.server.configs.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        /*
            title: Tiêu đề hiển thị swagger UI
            description: Mô tả API
            tất cả các endpoint sẽ yêu cầu security "BearerAuth"
        */
        info = @Info(title = "API Documentation", version = "1.0", description = "API for My Application"),
        security = @SecurityRequirement(name = "BearerAuth"),
        servers = @Server(url = "http://localhost:8080")
)
@SecurityScheme(
        /*
            securityScheme: định nghĩa cách bảo mật cho API
            name: tên scheme
            type: loại bảo mật HTTP Authentication
            scheme: Bearer Token
            format: JSON Web token
        */
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}
