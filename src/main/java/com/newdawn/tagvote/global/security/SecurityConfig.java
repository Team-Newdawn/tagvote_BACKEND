package com.newdawn.tagvote.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .securityContext(context -> context.requireExplicitSave(false))
                .anonymous(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration publicApiConfig = new CorsConfiguration();
        publicApiConfig.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://taglow-player.web.app",
                "https://taglow-participant.web.app",
                "https://taglow-admin.web.app",
                "https://admin.newdawnsoi.site",
                "https://taglow-acca6.web.app",
                "https://taglow-acca6.firebaseapp.com"
        ));
        publicApiConfig.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        publicApiConfig.setAllowedHeaders(List.of(
                "Accept",
                "Authorization",
                "Content-Type",
                "Origin",
                "X-Requested-With",
                "X-CSRF-TOKEN",
                "X-Taglow-Session-Id",
                "taglow-Session-Id"
        ));
        publicApiConfig.setExposedHeaders(List.of("Location"));
        publicApiConfig.setAllowCredentials(false);
        publicApiConfig.setMaxAge(3600L);

        CorsConfiguration privateApiConfig = new CorsConfiguration();
        privateApiConfig.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://taglow-admin.web.app",
                "https://admin.newdawnsoi.site"
        ));
        privateApiConfig.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        privateApiConfig.setAllowedHeaders(List.of(
                "Accept",
                "Authorization",
                "Content-Type",
                "Origin",
                "X-Requested-With",
                "X-CSRF-TOKEN"
        ));
        privateApiConfig.setExposedHeaders(List.of("Location"));
        privateApiConfig.setAllowCredentials(true);
        privateApiConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/public/**", publicApiConfig);
        source.registerCorsConfiguration("/api/**", privateApiConfig);
        return source;
    }
}
