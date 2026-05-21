package com.dazzling.blog.models;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "tag")
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String slug;

    @Setter
    private String name;

    public Tag() {}

    public Tag(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }
}