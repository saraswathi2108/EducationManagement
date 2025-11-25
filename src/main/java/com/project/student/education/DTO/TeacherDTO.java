package com.project.student.education.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDTO {

    private String teacherId;
    private String teacherName;
    private String email;
    private String phone;
    private String qualification;
    private String gender;
    private int experience;
    private String address;
    private String password;
    private List<String> subjectIds;
    private List<String> subjectNames;


}
