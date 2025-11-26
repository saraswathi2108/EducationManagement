package com.project.student.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolFeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String type; // "QUOTE" or "IMAGE" or "EVENT"
    private String title; // Title or Quote text
    private String description; // Description
    private String imageUrl; // Optional for Images
    private LocalDate postDate; // Date of posting
    private String postedBy; // "ADMIN"
}