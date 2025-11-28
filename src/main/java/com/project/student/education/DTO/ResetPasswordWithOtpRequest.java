package com.project.student.education.DTO;

import lombok.Data;

@Data
public class ResetPasswordWithOtpRequest {
    private String username;
    private String otp;
    private String newPassword;
    private String confirmNewPassword;
}
