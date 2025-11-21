package com.project.student.education.repository;

import com.project.student.education.entity.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassSectionRepository extends JpaRepository<ClassSection, String> {


    List<ClassSection> findByClassTeacher_TeacherId(String teacherId);

    Optional<ClassSection> findByClassNameAndAcademicYear(String className, String academicYear);


}
