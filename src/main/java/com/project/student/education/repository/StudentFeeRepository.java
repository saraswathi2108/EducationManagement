package com.project.student.education.repository;


import com.project.student.education.entity.StudentFee;
import com.project.student.education.enums.FeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee,String> {
    List<StudentFee> findByStudentId(String studentId);

    List<StudentFee> findByStudentIdAndStatusIn(String studentId, List<FeeStatus> statuses);
}
