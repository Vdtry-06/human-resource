package com.spring.human.resource.server.configs.security;

import com.spring.human.resource.server.caches.ICacheData;
import com.spring.human.resource.server.configs.language.DetectLanguageInterceptor;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final DetectLanguageInterceptor languageInterceptor;
    private final MessageSourceHelper messageSourceHelper;
    private final ICacheData<String> caches;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtProvider, userDetailsService, messageSourceHelper, caches);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(languageInterceptor);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /*
        @Bean: đánh dấu là 1 spring bean
        Method trả về đối tượng CorsConfigurationSource.
        CORS (CorsConfigurationSource): chia sẻ tài nguyên xuyên nguồn gốc => để kiểm soát request từ các domain khác
        cors.setAllowCredentials(false): không cho phép gửi cookie/ thông tin xác thực cùng request cross-origin
        cors.addAllowedOriginPattern(CorsConfiguration.ALL): cho phép mọi domain đều được gọi API (không giới hạn)
        cors.addAllowedMethod(CorsConfiguration.ALL): cho phép mọi HTTP method (GET, POST, PUT, DELETE, PATCH...)
        cors.addAllowedHeader(CorsConfiguration.ALL): cho phép mọi header trong request (Content-Type, Authorization, X-Requested-With...)
        source.registerCorsConfiguration("/**", cors): áp dụng rule CORS ở trên tất cả các endpoint
    */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowCredentials(false);
        cors.addAllowedOriginPattern(CorsConfiguration.ALL);
        cors.addAllowedMethod(CorsConfiguration.ALL);
        cors.addAllowedHeader(CorsConfiguration.ALL);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
