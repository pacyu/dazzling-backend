package com.dazzling.blog.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "article_like", uniqueConstraints = {@UniqueConstraint(columnNames = {"articleId", "userId"})})
@Getter
public class ArticleLike {
  
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
    private String userSlug;
    
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public ArticleLike() {}

    public ArticleLike(Long articleId, Long userId, String articleSlug, String userSlug) {
      this.articleId = articleId;
      this.userId = userId;
      this.articleSlug = articleSlug;
      this.userSlug = userSlug;
      this.createdAt = new Date();
    }
}
