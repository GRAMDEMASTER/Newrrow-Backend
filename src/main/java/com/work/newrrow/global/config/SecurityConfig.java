package com.work.newrrow.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/"))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/").permitAll()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions().disable());
        return http.build();
    }
}
