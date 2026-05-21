package com.dazzling.blog.repositories;

import com.dazzling.blog.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<List<Comment>> findByArticleId(Long articleId);

    Optional<Page<Comment>> findByArticleSlug(String articleSlug, Pageable pageable);

    Optional<List<Comment>> findByArticleSlug(String articleSlug);

    Page<Comment> findByArticleId(Long articleId, Pageable pageable);

    List<Comment> findByUserId(Long userId);

    @Query("SELECT c FROM Comment c WHERE c.articleId = :articleId AND c.id = :parentId")
    Optional<Comment> findByIdAndArticleId(
        @Param("articleId") Long articleId, @Param("parentId") Long parentId);
}