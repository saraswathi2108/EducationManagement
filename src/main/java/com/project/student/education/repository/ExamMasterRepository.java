package com.project.student.education.repository;

import com.project.student.education.entity.ExamMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamMasterRepository extends JpaRepository<ExamMaster, String> {
    List<ExamMaster> findByAcademicYear(String academicYear);

    boolean existsByExamNameAndAcademicYear(String examName, String academicYear);
}
