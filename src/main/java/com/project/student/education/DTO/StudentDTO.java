package com.project.student.education.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {

    private String studentId;
    private String admissionNumber;

    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String nationality;
    private String religion;
    private String category;
    private String aadhaarNumber;

    // 🔹 From ClassSection mapping
    private String classSectionId;
    private String grade;          // e.g., "6"
    private String section;        // e.g., "A"
    private String academicYear;   // e.g., "2025-2026"

    private LocalDate joiningDate;
    private String rollNumber;
    private String classTeacherId;


    private String address;
    private String city;
    private String state;
    private String pincode;
    private String contactNumber;
    private String email;

    private String fatherName;
    private String fatherContact;
    private String motherName;
    private String motherContact;
    private String guardianName;
    private String guardianContact;

    private String emergencyContactName;
    private String emergencyContactNumber;

    private String profileImageUrl;

    private Boolean active;


    private String generatedPassword;
    private Double totalFee;



}
