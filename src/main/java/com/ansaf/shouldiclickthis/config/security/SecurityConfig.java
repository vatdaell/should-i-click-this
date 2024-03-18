package com.ansaf.shouldiclickthis.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {
    @Autowired
    private BasicAuthenticationEntryPoint basicAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(POST, "/api/**").authenticated()
                        .requestMatchers(GET, "/actuator/**").permitAll()
                )
                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable()) // This is for H2 browser console access.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(this.basicAuthenticationEntryPoint))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
