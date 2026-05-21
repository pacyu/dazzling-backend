package com.dazzling.blog.controller;

import com.dazzling.blog.exception.ResourceNotFoundException;
import com.dazzling.blog.models.Article;
import com.dazzling.blog.models.Remote;
import com.dazzling.blog.repositories.ArticleRepository;
import com.dazzling.blog.repositories.RemoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/viewed")
public class ViewsRestController {

    @Autowired
    private RemoteRepository remoteRepository;
    
    @Autowired
    private ArticleRepository articleRepository;

    @PostMapping
    public ResponseEntity<?> viewed(
        HttpServletRequest request,
        @RequestBody Map<String, Object> payload
    ) throws ResourceNotFoundException {
        String slug = (String) payload.get("v");
        String remoteAddr = request.getRemoteAddr();
        Article article = articleRepository.findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("文章不存在！"));
        Remote remote = remoteRepository.findRemoteByMuilFields(article.getId(), remoteAddr);
        if (remote == null) {
            article.setViews(article.getViews() + 1L);
            articleRepository.save(article);
            remoteRepository.save(new Remote(article.getId(), article.getSlug(), remoteAddr));
            return ResponseEntity.ok(Map.of(
            "message", "Ok!",
            "status", 200
            ));
        } else {
            return ResponseEntity.ok(Map.of(
            "message", "Good!",
            "status", 201
            ));
        }
    }
}