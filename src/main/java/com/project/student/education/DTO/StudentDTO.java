package com.project.student.education.DTO;

import AIExpose.Agent.Annotations.AIExposeDto;
import AIExpose.Agent.Annotations.Describe;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AIExposeDto(
        name = "StudentDTO",
        description = "Represents an enrolled student with academic, personal, and contact information."
)
public class StudentDTO {

    @Describe(description = "Unique student ID.", dataType = "String", example = "STU2025004")
    private String studentId;

    @Describe(description = "Linked admission number.", dataType = "String", example = "ADM2025001")
    private String admissionNumber;

    @Describe(description = "Full name of the student.", dataType = "String", example = "Rohit Kumar")
    private String fullName;

    @Describe(description = "Student's date of birth.", dataType = "LocalDate")
    private LocalDate dateOfBirth;

    @Describe(description = "Student gender.", dataType = "String", example = "Male")
    private String gender;

    @Describe(description = "Blood group.", dataType = "String", example = "O+")
    private String bloodGroup;

    @Describe(description = "Nationality.", dataType = "String")
    private String nationality;

    @Describe(description = "Religion.", dataType = "String")
    private String religion;

    @Describe(description = "Student category.", dataType = "String")
    private String category;

    @Describe(description = "Aadhaar number.", dataType = "String")
    private String aadhaarNumber;

    @Describe(description = "Class section ID assigned to the student.", dataType = "String")
    private String classSectionId;

    @Describe(description = "Class or grade.", dataType = "String", example = "6")
    private String grade;

    @Describe(description = "Section letter.", dataType = "String", example = "A")
    private String section;

    @Describe(description = "Academic year.", dataType = "String", example = "2025-2026")
    private String academicYear;

    @Describe(description = "Date student joined the school.", dataType = "LocalDate")
    private LocalDate joiningDate;

    @Describe(description = "Roll number assigned to the student.", dataType = "String")
    private String rollNumber;

    @Describe(description = "Class teacher ID.", dataType = "String")
    private String classTeacherId;

    @Describe(description = "Full residential address.", dataType = "String")
    private String address;

    @Describe(description = "City.", dataType = "String")
    private String city;

    @Describe(description = "State.", dataType = "String")
    private String state;

    @Describe(description = "Postal code.", dataType = "String")
    private String pincode;

    @Describe(description = "Contact phone number.", dataType = "String")
    private String contactNumber;

    @Describe(description = "Email address.", dataType = "String")
    private String email;

    @Describe(description = "Father's full name.", dataType = "String")
    private String fatherName;

    @Describe(description = "Father's contact.", dataType = "String")
    private String fatherContact;

    @Describe(description = "Mother's full name.", dataType = "String")
    private String motherName;

    @Describe(description = "Mother's contact.", dataType = "String")
    private String motherContact;

    @Describe(description = "Guardian name.", dataType = "String")
    private String guardianName;

    @Describe(description = "Guardian contact.", dataType = "String")
    private String guardianContact;

    @Describe(description = "Emergency contact name.", dataType = "String")
    private String emergencyContactName;

    @Describe(description = "Emergency contact number.", dataType = "String")
    private String emergencyContactNumber;

    @Describe(description = "Profile image URL.", dataType = "String")
    private String profileImageUrl;

    @Describe(description = "Whether student is active or not.", dataType = "Boolean")
    private Boolean active;

    @Describe(description = "Password generated during admission approval.", dataType = "String")
    private String generatedPassword;

    @Describe(description = "Total annual fee assigned to the student.", dataType = "Double")
    private Double totalFee;
}
