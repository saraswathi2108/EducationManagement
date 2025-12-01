package com.project.student.education.DTO;

import AIExpose.Agent.Annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AIExposeDto(
        name = "CreateFeeRequest",
        description = "DTO used for creating a fee entry for a student. Works for both single and bulk fee creation operations."
)
public class CreateFeeRequest {

    @Describe(
            description = "ID of the student to whom the fee will be assigned.",
            dataType = "String",
            example = "STU2025001"
    )
    private String studentId;

    @Describe(
            description = "Name or label of the fee assigned to the student.",
            dataType = "String",
            example = "Tuition Fee"
    )
    private String feeName;

    @Describe(
            description = "Total amount to be paid for the fee.",
            dataType = "Double",
            example = "1500.0"
    )
    private Double amount;

    @Describe(
            description = "The due date for paying the fee.",
            dataType = "LocalDate",
            example = "2025-01-15"
    )
    private LocalDate dueDate;

    @JsonProperty("isExtra")
    @Describe(
            description = "Indicates whether this is an extra/manual fee. If true, the student's total fee is increased.",
            dataType = "boolean",
            example = "false"
    )
    private boolean isExtra;
}
