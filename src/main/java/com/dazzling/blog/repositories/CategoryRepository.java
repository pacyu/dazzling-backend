package com.dazzling.blog.repositories;

import com.dazzling.blog.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 按类别名称查询
    Category findByName(String name);

    // 正则查询转 LIKE
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :regex, '%'))")
    List<Category> findCategoriesByRegEx(@Param("regex") String regex);

    Optional<Category> findBySlug(String slug);

    Optional<Category> deleteBySlug(String slug);

    boolean existsBySlug(String slug);
}