package com.project.student.education.entity;


import com.project.student.education.enums.AdmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "admission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admission {

    @Id
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


    private String gradeApplied;
    private String academicYear;

    //private String section;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Enumerated(EnumType.STRING)
    private AdmissionStatus status;

    @OneToOne(mappedBy = "admission", cascade = CascadeType.ALL)
    private Student student;

    private String rejectionReason;
    private Double totalFee;

}
