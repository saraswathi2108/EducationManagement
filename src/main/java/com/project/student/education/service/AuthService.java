package com.project.student.education.service;

import com.project.student.education.DTO.*;
import com.project.student.education.entity.PasswordResetOTP;
import com.project.student.education.entity.User;
import com.project.student.education.enums.Role;
import com.project.student.education.repository.PasswordRepository;
import com.project.student.education.repository.UserRepository;
import com.project.student.education.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final AuthUtil authUtil;
    private final PasswordRepository otpRepository;
    private final JavaMailSender mailSender;


    public void signup(SignupRequestDto request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
    }


    public TokenPair login(LoginRequestDto request) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String accessToken = authUtil.generateAccessToken(user);
        String refreshToken = authUtil.generateRefreshToken(user);
        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair refreshToken(String refreshToken) {
        if (!authUtil.validateToken(refreshToken))
            throw new RuntimeException("Invalid refresh token");

        if (!authUtil.isRefreshToken(refreshToken))
            throw new RuntimeException("Token is not a refresh token");

        String username = authUtil.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccess = authUtil.generateAccessToken(user);
        String newRefresh = authUtil.generateRefreshToken(user);

        return new TokenPair(newAccess, newRefresh);
    }

    public String changePassword(String username, ChangePasswordRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password updated successfully";
    }


    private String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }



    @Transactional
    public String sendOtp(ForgotPasswordRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("Email not registered");
        }

        String otp = generateOtp();

        otpRepository.deleteByUsername(user.getUsername());

        otpRepository.save(
                PasswordResetOTP.builder()
                        .username(user.getUsername())
                        .otp(otp)
                        .expiryTime(LocalDateTime.now().plusMinutes(10))
                        .build()
        );


        sendEmail(user.getEmail(), otp, user.getUsername());

        return "OTP sent successfully";
    }
    private void sendEmail(String email, String otp, String username) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Password Reset OTP - " + username);
        msg.setText("Your OTP for password reset is: " + otp +
                "\n\nThis OTP expires in 10 minutes.");

        mailSender.send(msg);
    }

    @Transactional
    public String resetPassword(ResetPasswordWithOtpRequest request) {

        PasswordResetOTP otpData = otpRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Please request OTP again"));

        if (otpData.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.deleteByUsername(request.getUsername());
            throw new RuntimeException("OTP expired");
        }

        if (!otpData.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpRepository.deleteByUsername(request.getUsername());

        return "Password reset successfully";
    }
}

