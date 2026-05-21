package com.dazzling.blog.models;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comment")
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long articleId;

    @Setter
    private Long userId;

    @Setter
    private String articleSlug;

    @Setter
    private Long parentId;

    @Setter
    private Long rootId;

    @Setter
    private Long depth;

    @Setter
    @Column(length = 2000)
    private String content;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Setter
    private Boolean isEdit;

    public Comment() {}

    public Comment(
        Long articleId, Long userId, String articleSlug,
        Long parentId, Long rootId, Long depth, String content
    ) {
        this.articleId = articleId;
        this.userId = userId;
        this.articleSlug = articleSlug;
        this.parentId = parentId;
        this.rootId = rootId;
        this.depth = depth;
        this.content = content;
        this.createdAt = new Date();
        this.isEdit = false;
    }
}