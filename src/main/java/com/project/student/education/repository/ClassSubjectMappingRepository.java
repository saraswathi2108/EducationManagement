package com.project.student.education.repository;

import com.project.student.education.entity.ClassSection;
import com.project.student.education.entity.ClassSubjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassSubjectMappingRepository extends JpaRepository<ClassSubjectMapping, String> {

    List<ClassSubjectMapping> findByClassSection_ClassSectionId(String classSectionId);

    Optional<ClassSubjectMapping> findByClassSection_ClassSectionIdAndSubject_SubjectId(
            String classSectionId, String subjectId
    );

    List<ClassSubjectMapping> findBySubject_SubjectId(String subjectId);

    boolean existsByClassSection_ClassSectionIdAndSubject_SubjectId(String classSectionId, String subjectId);


    void deleteByClassSection(ClassSection classSection);

    List<ClassSubjectMapping> findByTeacher_TeacherId(String teacherId);
}
