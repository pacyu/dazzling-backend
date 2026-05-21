package com.dazzling.blog.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "remote")
@Getter
public class Remote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long articleId;
    
    @Setter
    private String articleSlug;

    @Setter
    private String address;

    public Remote() {}

    public Remote(Long articleId, String articleSlug, String address) {
        this.articleId = articleId;
        this.articleSlug = articleSlug;
        this.address = address;
    }
}