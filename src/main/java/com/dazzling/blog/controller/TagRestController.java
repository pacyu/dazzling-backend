package com.dazzling.blog.controller;

import com.dazzling.blog.exception.ResourceNotFoundException;
import com.dazzling.blog.models.Tag;
import com.dazzling.blog.repositories.TagRepository;
import com.dazzling.blog.utils.SlugUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RestController
@RequestMapping("/api/tag")
public class TagRestController {

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @GetMapping(params = "v")
    public Tag getBySlug(@RequestParam(value = "v") String slug) {
        return tagRepository.findBySlug(slug).orElseThrow(() -> 
        new ResourceNotFoundException("未找到标签！"));
    }

    @GetMapping(params = "id")
    public Tag getById(@RequestParam(value = "id") Number id) {
        return tagRepository.findById(id.longValue()).orElseThrow(() -> 
        new ResourceNotFoundException("未找到标签！"));
    }

    @PostMapping
    public ResponseEntity<?> newTag(@RequestBody Map<String, String> payload) {
        String tag = payload.get("name");
        tagRepository.save(new Tag(SlugUtil.randomSlug(7), tag));
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "添加成功！"
        ));
    }

    @PutMapping
    public ResponseEntity<?> updateTag(
        @RequestBody Map<String, Object> payload
    ) {
        String slug = (String) payload.get("slug");
        String name = (String) payload.get("name");
        Tag tag = tagRepository.findBySlug(slug).orElseThrow(() -> 
        new ResourceNotFoundException("未找到标签！"));

        tag.setName(name);
        tagRepository.save(tag);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "修改成功！"
        ));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTag(
        @RequestBody Map<String, Object> payload
    ) {
        Long id = ((Number) payload.get("id")).longValue();
        tagRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "删除成功！"
        ));
    }
}