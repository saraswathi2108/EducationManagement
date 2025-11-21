package com.project.student.education.repository;

import com.project.student.education.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,String> {
  //  List<Subject> findByClassSection_ClassSectionId(String classSectionId);

   // List<Subject> findByTeacher_TeacherId(String teacherId);

    Optional<Subject> findBySubjectNameIgnoreCase(String name);

    Optional<Subject> findBySubjectCodeIgnoreCase(String code);

}
