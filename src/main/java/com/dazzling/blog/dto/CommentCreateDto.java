package com.dazzling.blog.dto;

import lombok.Data;

@Data
public class CommentCreateDto {
    private Long parentId;
    private String slug;
    private String username;
    private String email;
    private String content;
}