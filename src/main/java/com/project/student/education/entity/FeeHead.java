package com.project.student.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class FeeHead {

    @Id
    private String id;

    private String name;        // e.g. "TUITION", "TRANSPORT", "ANNUAL", "EXAM"

    // How this fee recurs
    // MONTHLY, YEARLY, TERM, ONE_TIME
    private String frequency;

    private String description;
}
