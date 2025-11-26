package com.project.student.education.repository;

import com.project.student.education.entity.SchoolFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolFeedRepository extends JpaRepository<SchoolFeed, Integer> {
}
