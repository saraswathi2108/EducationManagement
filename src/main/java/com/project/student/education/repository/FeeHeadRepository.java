package com.project.student.education.repository;

import com.project.student.education.entity.FeeHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeHeadRepository extends JpaRepository<FeeHead, String> {

    Optional<FeeHead> findByName(String name);

    boolean existsByName(String name);
}
