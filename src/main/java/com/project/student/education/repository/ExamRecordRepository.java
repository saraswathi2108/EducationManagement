package com.project.student.education.repository;

import com.project.student.education.entity.ExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ExamRecordRepository extends JpaRepository<ExamRecord, Long> {

    List<ExamRecord> findByExamIdAndStudentId(String examId, String studentId);

    List<ExamRecord> findByExamIdAndClassSectionId(String examId, String classSectionId);

    List<ExamRecord> findByExamIdAndClassSectionIdAndSubjectId(String examId, String classSectionId, String subjectId);

    @Query(value = """
        SELECT rnk FROM (
            SELECT student_id,
                   SUM( (paper_obtained + assignment_obtained) ) AS totalMarks,
                   RANK() OVER (ORDER BY SUM(paper_obtained + assignment_obtained) DESC ) AS rnk
            FROM exam_record
            WHERE exam_id = :examId AND class_section_id = :classSectionId
            GROUP BY student_id
        ) t WHERE t.student_id = :studentId
    """, nativeQuery = true)
    Integer calculateRank(String examId, String classSectionId, String studentId);

    boolean existsByExamIdAndClassSectionIdAndSubjectIdAndStudentId(String examId, String classSectionId, String subjectId, String studentId);

    boolean existsByExamIdAndClassSectionIdAndExamDateAndStartTimeAndEndTime(String examId, String classId, LocalDate examDate, LocalTime startTime, LocalTime endTime);
}
