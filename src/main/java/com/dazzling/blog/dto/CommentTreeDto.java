package com.dazzling.blog.dto;

import com.dazzling.blog.models.Comment;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class CommentTreeDto {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String email;
    private Long parentId;
    private Long rootId;
    private Long depth;
    private Date createdAt;
    private List<CommentTreeDto> replies = new ArrayList<>();

    public CommentTreeDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.parentId = comment.getParentId();
        this.rootId = comment.getRootId();
        this.depth = comment.getDepth();
        this.createdAt = comment.getCreatedAt();
    }
}