package com.project.student.education.DTO;

import AIExpose.Agent.Annotations.AIExposeDto;
import AIExpose.Agent.Annotations.Describe;
import com.project.student.education.enums.AdmissionStatus;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AIExposeDto(
        name = "AdmissionDTO",
        description = "Represents an admission application submitted by a student or parent."
)
public class AdmissionDTO {

    @Describe(description = "Unique admission number.", dataType = "String", example = "ADM2025001")
    private String admissionNumber;

    @Describe(description = "Date on which admission was submitted.", dataType = "LocalDate", example = "2025-01-06")
    private LocalDate admissionDate;

    @Describe(description = "Applicant's full name.", dataType = "String", example = "Rohit Kumar")
    private String applicantName;

    @Describe(description = "Applicant's date of birth.", dataType = "LocalDate", example = "2015-05-14")
    private LocalDate dateOfBirth;

    @Describe(description = "Gender of the applicant.", dataType = "String", example = "Male")
    private String gender;

    @Describe(description = "Applicant's blood group.", dataType = "String", example = "O+")
    private String bloodGroup;

    @Describe(description = "Nationality of the applicant.", dataType = "String", example = "Indian")
    private String nationality;

    @Describe(description = "Religion of the applicant.", dataType = "String", example = "Hindu")
    private String religion;

    @Describe(description = "Category (GEN/OBC/SC/ST/etc.).", dataType = "String", example = "OBC")
    private String category;

    @Describe(description = "Aadhaar number of the applicant.", dataType = "String", example = "1234-5678-9012")
    private String aadhaarNumber;

    @Describe(description = "Total school fee for the student's grade.", dataType = "Double", example = "35000")
    private Double totalFee;

    @Describe(description = "Grade applied for.", dataType = "String", example = "6")
    private String gradeApplied;

    @Describe(description = "Academic year of admission.", dataType = "String", example = "2025-2026")
    private String academicYear;

    @Describe(description = "Name of previous school attended.", dataType = "String", example = "ABC Public School")
    private String previousSchool;

    @Describe(description = "Class attended in previous school.", dataType = "String", example = "5")
    private String previousClass;

    @Describe(description = "Medium of instruction.", dataType = "String", example = "English")
    private String mediumOfInstruction;

    @Describe(description = "Father's full name.", dataType = "String", example = "Ramesh Kumar")
    private String fatherName;

    @Describe(description = "Father's occupation.", dataType = "String", example = "Engineer")
    private String fatherOccupation;

    @Describe(description = "Father's contact number.", dataType = "String", example = "9876543210")
    private String fatherContact;

    @Describe(description = "Mother's full name.", dataType = "String", example = "Sita Devi")
    private String motherName;

    @Describe(description = "Mother's occupation.", dataType = "String", example = "Teacher")
    private String motherOccupation;

    @Describe(description = "Mother's contact number.", dataType = "String", example = "9876501234")
    private String motherContact;

    @Describe(description = "Guardian name, if applicable.", dataType = "String")
    private String guardianName;

    @Describe(description = "Guardian relation.", dataType = "String")
    private String guardianRelation;

    @Describe(description = "Guardian contact number.", dataType = "String")
    private String guardianContact;

    @Describe(description = "Full home address.", dataType = "String")
    private String address;

    @Describe(description = "City.", dataType = "String")
    private String city;

    @Describe(description = "State.", dataType = "String")
    private String state;

    @Describe(description = "Pincode.", dataType = "String")
    private String pincode;

    @Describe(description = "Emergency contact person name.", dataType = "String")
    private String emergencyContactName;

    @Describe(description = "Emergency contact number.", dataType = "String")
    private String emergencyContactNumber;

    @Describe(description = "Birth certificate file URL.", dataType = "String")
    private String birthCertificateUrl;

    @Describe(description = "Transfer certificate file URL.", dataType = "String")
    private String transferCertificateUrl;

    @Describe(description = "Aadhaar card file URL.", dataType = "String")
    private String aadhaarCardUrl;

    @Describe(description = "Aadhaar card number.", dataType = "String")
    private String aadhaarCardNumber;

    @Describe(description = "Profile photo URL uploaded by the applicant.", dataType = "String")
    private String photoUrl;

    @Describe(description = "Remarks added by admin during review.", dataType = "String")
    private String remarks;

    @Describe(description = "Admin user who approved the admission.", dataType = "String")
    private String approvedBy;

    @Describe(description = "Date on which admission was approved.", dataType = "LocalDate")
    private LocalDate approvedDate;

    @Describe(description = "Admission status.", dataType = "AdmissionStatus", example = "PENDING")
    private AdmissionStatus status;

    @Describe(description = "Reason for rejection if admission was rejected.", dataType = "String")
    private String rejectionReason;

    @Describe(description = "Applicant's email address.", dataType = "String")
    private String email;
}
