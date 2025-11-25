package com.project.student.education.repository;

import com.project.student.education.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(t) from Teacher t")
    Long countTeachers();


    Optional<Teacher> findByUser_Username(String loginName);

    Optional<Teacher> findByUser_Id(long id);
}
