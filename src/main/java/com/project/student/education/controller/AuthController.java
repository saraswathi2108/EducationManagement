package com.project.student.education.controller;

import com.project.student.education.DTO.*;
import com.project.student.education.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto request) {
        authService.signup(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        TokenPair tokenPair = authService.login(request);
        return ResponseEntity.ok(Map.of(
                "accessToken", tokenPair.getAccessToken(),
                "refreshToken", tokenPair.getRefreshToken()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "User logged out successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing refresh token"));
        }

        TokenPair newTokens = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", newTokens.getAccessToken(),
                "refreshToken", newTokens.getRefreshToken()
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(authService.changePassword(username, request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", authService.sendOtp(req)
        ));

    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordWithOtpRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", authService.resetPassword(req)
        ));
    }

}
