package com.project.student.education.repository;

import com.project.student.education.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);



    @Query("SELECT u.username FROM User u WHERE u.role = com.project.student.education.enums.Role.ROLE_ADMIN")
    List<String> findAdminUsernames();
}
