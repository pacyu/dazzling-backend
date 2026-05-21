package com.dazzling.blog.controller;

import com.dazzling.blog.exception.ResourceNotFoundException;
import com.dazzling.blog.models.Article;
import com.dazzling.blog.models.ArticleLike;
import com.dazzling.blog.repositories.ArticleLikeRepository;
import com.dazzling.blog.repositories.ArticleRepository;
import com.dazzling.blog.repositories.UserRepository;
import com.dazzling.blog.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/liked")
public class LikeRestController {

    @Autowired
    private ArticleLikeRepository articleLikeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> liked(
        Principal principal,
        @RequestBody Map<String, Object> payload
    ) throws ResourceNotFoundException {
        String slug = (String) payload.get("v");
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("用户未登录，该功能不对游客开放！"));
        Article article = articleRepository.findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("文章不存在！"));
        
        ArticleLike articleLike = articleLikeRepository.findByArticleIdAndUserId(article.getId(), user.getId()).orElse(null);
        if (articleLike == null) {
            articleLike = new ArticleLike(article.getId(), user.getId(), slug, user.getSlug());
            articleLikeRepository.save(articleLike);

            article.setLikes(article.getLikes() + 1L);
            articleRepository.save(article);
            return ResponseEntity.ok(Map.of(
            "message", "点赞成功!",
            "status", 200
            ));
        } else {
            articleLikeRepository.deleteById(articleLike.getId());

            article.setLikes(article.getLikes() - 1L);
            articleRepository.save(article);
            return ResponseEntity.ok(Map.of(
            "message", "取消点赞成功！",
            "status", 200
            ));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> cancledLike(
        Principal principal,
        @RequestBody Map<String, Object> payload
    ) throws ResourceNotFoundException {
        String slug = (String) payload.get("v");
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("用户未登录，该功能不对游客开放！"));
        Article article = articleRepository.findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("文章不存在！"));

        ArticleLike articleLike = articleLikeRepository.findByArticleIdAndUserId(article.getId(), user.getId()).orElse(null);
        if (articleLike == null) {
            return ResponseEntity.ok(Map.of(
                "message", "删除点赞失败！",
                "status", 200
                ));
        }

        articleLikeRepository.deleteById(articleLike.getId());

        article.setLikes(article.getLikes() - 1L);
        articleRepository.save(article);
        return ResponseEntity.ok(Map.of(
        "message", "删除点赞成功！",
        "status", 200
        ));
    }
}