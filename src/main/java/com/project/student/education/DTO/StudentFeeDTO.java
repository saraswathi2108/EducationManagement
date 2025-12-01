package com.project.student.education.DTO;

import AIExpose.Agent.Annotations.AIExposeDto;
import AIExpose.Agent.Annotations.Describe;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AIExposeDto(
        name = "StudentFeeDTO",
        description = "Represents a fee entry assigned to a student, including fee amount, paid amount, due date, and status.",
        example = "{\n" +
                "  \"feeId\": \"FEE20250001\",\n" +
                "  \"feeName\": \"Term Fee\",\n" +
                "  \"amount\": 5000,\n" +
                "  \"amountPaid\": 2000,\n" +
                "  \"dueDate\": \"2025-12-01\",\n" +
                "  \"status\": \"PARTIAL\"\n" +
                "}"
)
public class StudentFeeDTO {

    @Describe(
            description = "Unique ID generated for each fee record.",
            dataType = "String",
            example = "FEE20250001"
    )
    private String feeId;

    @Describe(
            description = "Name of the fee assigned to the student.",
            dataType = "String",
            example = "Term Fee"
    )
    private String feeName;

    @Describe(
            description = "Total fee amount required to be paid.",
            dataType = "Double",
            example = "5000.00"
    )
    private Double amount;

    @Describe(
            description = "Amount already paid by the student.",
            dataType = "Double",
            example = "2000.00"
    )
    private Double amountPaid;

    @Describe(
            description = "Due date by which the fee should be paid.",
            dataType = "LocalDate",
            example = "2025-12-01"
    )
    private LocalDate dueDate;

    @Describe(
            description = "Current payment status of this fee entry (PENDING, PARTIAL, PAID, OVERDUE).",
            dataType = "String",
            example = "PARTIAL"
    )
    private String status;
}
