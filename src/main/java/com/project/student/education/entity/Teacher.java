package com.project.student.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher  {

    @Id
    private String teacherId;

    @Column(nullable = false)
    private String teacherName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String qualification;
    private String gender;
    private int experience;

    private String address;



    @ElementCollection
    @CollectionTable(
            name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher_id")
    )
    @Column(name = "subject_id")
    private List<String> subjectIds;



    @OneToMany(mappedBy = "classTeacher")
    private List<ClassSection> assignedSection;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


}
