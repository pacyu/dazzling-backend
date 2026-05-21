package com.dazzling.blog.models;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "category")
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String slug;

    @Setter
    private String name;

    @Setter
    private String cover;

    public Category() {}

    public Category(String slug, String name, String cover) {
        this.slug = slug;
        this.name = name;
        this.cover = cover;
    }
}