package com.realestate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disable CSRF (useful for testing)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow everything
                )
                .formLogin().disable() // Disable default login form
                .httpBasic().disable(); // Disable basic authentication

        return http.build();
    }
}
