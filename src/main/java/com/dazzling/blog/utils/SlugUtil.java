package com.dazzling.blog.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.function.Function;

public class SlugUtil {
    private static final SecureRandom random = new SecureRandom();
    private static final int DEFAULT_LENGTH = 11;

    /**
     * 生成指定长度的随机 URL-safe 字符串（字母数字 + 短横线和下划线）
     * 例如: "dQw4w9WgXcQ"
     */
    public static String randomSlug(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        // 使用 Base64 URL-safe 编码，去掉末尾的 '='
        String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        // 如果长度超过，截取；如果不够，再补充（几乎不会发生）
        return base64.length() > length ? base64.substring(0, length) : base64;
    }

    public static String randomSlug() {
        return randomSlug(DEFAULT_LENGTH);
    }

    /**
     * 生成唯一 slug，支持检查重复
     * @param existingChecker 检查数据库中是否存在该 slug 的函数
     * @return 唯一的 slug
     */
    public static String uniqueSlug(Function<String, Boolean> existingChecker) {
        String slug;
        do {
            slug = randomSlug();
        } while (existingChecker.apply(slug));
        return slug;
    }
}