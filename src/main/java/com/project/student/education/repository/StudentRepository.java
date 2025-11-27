package com.project.student.education.repository;

import com.project.student.education.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByClassSection_ClassSectionId(String classSectionId);



    @Query("SELECT s.studentId FROM Student s WHERE s.classSection.classSectionId = :classSectionId")
    List<String> findStudentIdsByClassSectionId(String classSectionId);

    @Query("SELECT COUNT(s) FROM Student s")
    Long countStudents();

    @Query("""
    SELECT s.gender,
           COUNT(s) * 100.0 / (SELECT COUNT(st) FROM Student st)
    FROM Student s
    GROUP BY s.gender
""")
    List<Object[]> getGenderPercentage();

    Collection<Object> findByGradeAndClassSectionIsNull(String grade);

    @Query("SELECT SUM(s.totalFee) FROM Student s WHERE s.classSection.classSectionId = :classSectionId")
    Double getTotalFeeByClass(@Param("classSectionId") String classSectionId);

    int countByClassSection_ClassSectionId(String classSectionId);

    @Query("SELECT s.studentId FROM Student s")
    List<String> findAllStudentIds();
}
