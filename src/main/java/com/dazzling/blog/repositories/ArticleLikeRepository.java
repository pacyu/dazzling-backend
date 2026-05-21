package com.dazzling.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dazzling.blog.models.ArticleLike;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
  
  Optional<ArticleLike> findByArticleIdAndUserId(Long articleId, Long userId);
}
