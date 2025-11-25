package com.project.student.education.DTO;

import com.project.student.education.enums.AdmissionStatus;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionDTO {

    private String admissionNumber;
    private LocalDate admissionDate;


    private String applicantName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String nationality;
    private String religion;
    private String category;
    private String aadhaarNumber;
    private Double totalFee;



    private String gradeApplied;
    private String academicYear;

    private String previousSchool;
    private String previousClass;
    private String mediumOfInstruction;


    private String fatherName;
    private String fatherOccupation;
    private String fatherContact;

    private String motherName;
    private String motherOccupation;
    private String motherContact;

    private String guardianName;
    private String guardianRelation;
    private String guardianContact;


    private String address;
    private String city;
    private String state;
    private String pincode;


    private String emergencyContactName;
    private String emergencyContactNumber;


    private String birthCertificateUrl;
    private String transferCertificateUrl;
    private String aadhaarCardUrl;
    private String aadhaarCardNumber;
    private String photoUrl;


    private String remarks;
    private String approvedBy;
    private LocalDate approvedDate;

    private AdmissionStatus status;
    private String rejectionReason;
}
