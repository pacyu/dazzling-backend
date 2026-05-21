package com.dazzling.blog.repositories;

import com.dazzling.blog.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);

    Optional<Tag> findBySlug(String slug);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :regex, '%'))")
    List<Tag> findTagsByRegEx(@Param("regex") String regex);
}