package com.dazzling.blog.models;

import com.dazzling.blog.converter.StringListConverter;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.Date;
import java.util.ArrayList;

@Entity
@Table(name = "article")
@Getter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Setter
    private String slug;

    @Setter
    private Long userId;

    @Setter
    private String userSlug;

    @Setter
    private String title;

    @Setter
    @Column(length = 500)
    private String introduction;

    @Setter
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Setter
    private String cover;

    // ArrayList<String> 转为用逗号分隔的字符串存到单个字段
    @Setter
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "VARCHAR(2000)")
    private ArrayList<String> category;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date releasedAt;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Setter
    private String author;

    @Setter
    private String hashString;

    @Setter
    private long editCount;

    @Setter
    private long likes;

    @Setter
    private long views;

    @Setter
    private long reviews;

    @Setter
    private Boolean isRemove;

    @Setter
    private Boolean isDraft;

    @Setter
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "VARCHAR(2000)")
    private ArrayList<String> tag;

    public Article() {}

    public Article(
        String slug, Long userId, String userSlug, String title, String introduction,
        String content, String cover, ArrayList<String> category,
        Date createdAt, Date releasedAt, Date updatedAt, String author,
        long editCount, long likes, long views, long reviews,
        Boolean isRemove, Boolean isDraft, ArrayList<String> tag
    ) {
        this.slug = slug;
        this.userId = userId;
        this.userSlug = userSlug;
        this.title = title;
        this.introduction = introduction;
        this.content = content;
        this.cover = cover;
        this.category = category;
        this.createdAt = createdAt;
        this.releasedAt = releasedAt;
        this.updatedAt = updatedAt;
        this.author = author;
        this.editCount = editCount;
        this.likes = likes;
        this.views = views;
        this.reviews = reviews;
        this.isRemove = isRemove;
        this.isDraft = isDraft;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return String.format("Article[id=%d, title='%s', introduction='%s', author='%s']",
                id, title, introduction, author);
    }

    public String genMd5() {
        try {
            String text = title + createdAt + id;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(text.getBytes());
            java.math.BigInteger no = new java.math.BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}