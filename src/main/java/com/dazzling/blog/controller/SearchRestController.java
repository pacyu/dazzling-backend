package com.dazzling.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.dazzling.blog.models.Article;
import com.dazzling.blog.repositories.ArticleRepository;

import lombok.NonNull;

@RestController
@RequestMapping("/api/search")
public class SearchRestController {
  @Autowired
	private ArticleRepository articleRepository;

  @GetMapping
	public Page<Article> getArticlesByPagebean(@NonNull @PageableDefault(size = 10) Pageable pageable) {
		return articleRepository.findAll(pageable);
	}

  @GetMapping(params = "q")
	public Page<Article> searchByKeyword(
    @RequestParam(value = "q") String keyword,
    @NonNull @PageableDefault(size = 10) Pageable pageable
  ) {
		return articleRepository.findArticlesByRegEx(keyword, pageable);
	}
}
