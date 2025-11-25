package com.project.student.education.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notice {

    @Id
    private String id;
    private String noticeName;
    private String noticeDescription;
    private String noticeType;
    private LocalDate noticeDate;
}
