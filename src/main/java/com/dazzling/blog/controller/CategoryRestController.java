package com.dazzling.blog.controller;

import com.dazzling.blog.exception.ResourceNotFoundException;
import com.dazzling.blog.models.Category;
import com.dazzling.blog.repositories.CategoryRepository;
import com.dazzling.blog.service.ImageDownloadService;
import com.dazzling.blog.service.ImageService;
import com.dazzling.blog.utils.SlugUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;

import java.util.*;

@RestController
@RequestMapping("/api/category")
public class CategoryRestController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDownloadService imageDownloadService;

    @Value("${file.upload.coverPath}")
    private String coverPath;
    
	@Value("${pollinations.ai.prompt}")
	private String prompt;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping(params = "id")
    public Category getById(@RequestParam(value = "{id}", required = false, defaultValue = "") Number id) {
        return categoryRepository.findById(id.longValue()).orElseThrow();
    }

    @GetMapping(params = "v")
    public Category getBySlug(
        @RequestParam(value = "{v}", required = false, defaultValue = "") String slug
    ) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> 
                new ResourceNotFoundException("未找到分类！"));
    }

    @PostMapping
    public ResponseEntity<?> newCategory(
        @RequestBody Map<String, String> payload
    ) throws DataAccessException, MethodArgumentNotValidException {

        String name = payload.get("name");
        String cover = payload.get("cover");

        String slug = SlugUtil.randomSlug(7);

        if (cover == "") {
            cover = slug + ".png";

            byte[] coverImage = imageService.generateCover(prompt);

            imageDownloadService.saveImage(coverImage, coverPath + cover);
        }
        
        Category category = new Category(slug, name, cover);
        categoryRepository.save(category);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "添加成功！"
        ));
    }

    @PutMapping
    public ResponseEntity<?> updateCategory(
        @RequestBody Map<String, String> payload
    ) throws DataAccessException, MethodArgumentNotValidException {

        String slug = payload.get("v");
        String name = payload.get("name");
        String cover = payload.get("cover");

        Category category = categoryRepository.findBySlug(slug)
        .orElseThrow(() -> 
        new ResourceNotFoundException("未找到分类！"));

        if (cover == "") {
            cover = slug + ".png";
            byte[] coverImage = imageService.generateCover(prompt);
            imageDownloadService.saveImage(coverImage, coverPath + cover);
        }

        category.setName(name);
        category.setCover(cover);
        categoryRepository.save(category);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "修改成功!"
        ));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCategory(
        @RequestBody Map<String, Object> payload
    ) throws DataAccessException, MethodArgumentNotValidException {

        Long id = ((Number) payload.get("id")).longValue();

        categoryRepository.deleteById(id);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "删除成功！"
        ));
    }
}