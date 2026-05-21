package com.dazzling.blog.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String slug;

    @Setter
    private String email;

    @Setter
    private String username;

    @Setter
    private String password;

    @Setter
    private String avatar;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Setter
    private String role;

    public User() {}

    public User(String email) {
        this.email = email;
    }

    public User(String slug, String email, String username, String password, String avatar, String role) {
        this.slug = slug;
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.createdAt = new Date();
        this.role = role;
    }
}