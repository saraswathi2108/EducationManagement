package com.project.student.education.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthUtil jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        // 🌟 Public endpoints - no authentication required
        if (isPublicEndpoint(path)) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");

        // 🌟 If header contains Bearer token → authenticate
        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);

                UserDetails user = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                // 🌟 Register user as authenticated
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(req, res);
    }
    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/student/auth/login")
                || path.equals("/api/student/auth/signup")
                || path.equals("/api/student/auth/refresh-token")
                || path.equals("/api/student/auth/forgot-password")
                || path.equals("/api/student/auth/reset-password")
                || path.startsWith("/api/student/notifications/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")
                || path.startsWith("/images/");
    }

}
