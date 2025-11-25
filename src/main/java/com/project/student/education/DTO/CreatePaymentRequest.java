package com.project.student.education.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {
    private String feeId;
    private Double amount;
    private String method;
}