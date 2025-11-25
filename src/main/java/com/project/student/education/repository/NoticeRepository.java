package com.project.student.education.repository;


import com.project.student.education.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository  extends JpaRepository<Notice, String> {
    boolean existsByNoticeName(String noticeName);
}
