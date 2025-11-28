package com.project.student.education.config;

import com.project.student.education.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    // ---------------- PASSWORD ENCODER ----------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---------------- AUTH PROVIDER ----------------
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ---------------- AUTH MANAGER ----------------
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ---------------- UNIVERSAL CORS (WEB + MOBILE) ----------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 🔥 Allow ALL domains (Web + Mobile + IP + Android emulator + iOS)
        config.setAllowedOriginPatterns(Arrays.asList("*"));

        // 🔥 Required for file upload, POST, PUT, PATCH
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 🔥 Allow headers for JWT + Multipart
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // 🔥 Allow cookies / auth tokens for mobile apps
        config.setAllowCredentials(true);

        // 🔥 Expose tokens to frontend
        config.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // ---------------- SECURITY FILTER CHAIN ----------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                // 🔥 THIS IS THE IMPORTANT LINE FOR WEB + MOBILE CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h.frameOptions(f -> f.deny()))

                .authorizeHttpRequests(auth -> auth

                        // ---------- Public Endpoints ----------
                        .requestMatchers(
                                "/api/student/auth/login",
                                "/api/student/auth/signup",
                                "/api/student/auth/refresh-token",
                                "/api/student/auth/forgot-password",
                                "/api/student/auth/reset-password",
                                "/api/student/admission",
                                "/api/student/admissions",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**",

                                "/api/student/notifications/**"
                        ).permitAll()
                        .requestMatchers("/images/**").permitAll()


                        // ---------- Protected ----------
                        .requestMatchers("/api/student/auth/change-password").authenticated()

                        // ---------- Everything else requires login ----------
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
