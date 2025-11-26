package com.project.student.education.repository;


import com.project.student.education.entity.StudentFee;
import com.project.student.education.enums.FeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee,String> {
    List<StudentFee> findByStudentId(String studentId);

    List<StudentFee> findByStudentIdAndStatusIn(String studentId, List<FeeStatus> statuses);





    // 2. Get total fee expected from a class (Sum of 'totalFee' field in Student entity)
    @Query("SELECT SUM(s.totalFee) FROM Student s WHERE s.classSection.classSectionId = :classSectionId")
    Double getTotalFeeByClass(@Param("classSectionId") String classSectionId);

    @Query("SELECT SUM(sf.amountPaid) FROM StudentFee sf JOIN Student s ON sf.studentId = s.studentId WHERE s.classSection.classSectionId = :classSectionId")
    Double getTotalPaidByClass(@Param("classSectionId") String classSectionId);
}
