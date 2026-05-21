package com.dazzling.blog.repositories;

import com.dazzling.blog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    User findByEmail(String email);

    Optional<User> findBySlug(String slug);

    boolean existsBySlug(String slug);
}