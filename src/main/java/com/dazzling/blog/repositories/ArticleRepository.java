package com.dazzling.blog.repositories;

import com.dazzling.blog.models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 原 MongoDB 的 $or 正则查询，改为 JPQL 使用 LIKE（不区分大小写，匹配任意位置）
    @Query(value = "SELECT * FROM Article a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.tag) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.category) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    Page<Article> findArticlesByRegEx(@Param("keyword") String keyword, Pageable pageable);

    // 原方法 findByReleasedAt(String releasedAt)，需要将参数转为 Date 比较
    // 假设 releasedAt 格式是 "yyyy-MM-dd"，可以用 @Param + 类型转换，或改为接收 Date 参数
    // 这里简单改为按日期部分比较（忽略时间）
    @Query("SELECT a FROM Article a WHERE DATE(a.releasedAt) = DATE(:releasedAt)")
    Article findByReleasedAt(@Param("releasedAt") Date releasedAt);

    // 按标题查询
    Optional<Article> findByTitle(String title);

    // 按分类查询（分类是 ArrayList<String>，在 JPA 中不能用简单相等，需改逻辑）
    // 原 MongoDB 查询是 {'category': {$eq: '?0'}}，表示 category 数组包含该字符串。
    // 在 JPA 中，因为 category 被转换成了逗号分隔的字符串，可以用 LIKE 模糊匹配
    @Query("SELECT a FROM Article a WHERE a.category LIKE CONCAT('%', :category, '%')")
    List<Article> findByCategory(@Param("category") String category);

    @Query("SELECT a FROM Article a WHERE a.category LIKE CONCAT('%', :category, '%')")
    Page<Article> findByCategory(@Param("category") String category, Pageable pageable);

    // 封面图片正则查询转 LIKE
    @Query("SELECT a FROM Article a WHERE LOWER(a.cover) LIKE LOWER(CONCAT('%', :regex, '%'))")
    Page<Article> findArticleByCoverRegEx(@Param("regex") String regex, Pageable pageable);

    Optional<Article> findBySlug(String slug);

    Optional<Article> deleteBySlug(String slug);

    boolean existsBySlug(String slug);
}