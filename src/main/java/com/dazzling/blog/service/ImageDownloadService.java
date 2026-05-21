package com.dazzling.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.file.*;

@Service
public class ImageDownloadService {
  
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 从 URL 下载图片并保存到本地
     * @param imageUrl 图片的URL
     * @param savePath 本地保存路径，如：/uploads/avatars/user123.png
     * @return 是否保存成功
     */
    public boolean downloadAndSave(String imageUrl, String savePath) {
        try {
            // 1. 调用API获取图片二进制数据
            byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
            if (imageBytes == null) return false;

            Path path = Paths.get(savePath);
            Files.createDirectories(path.getParent());

            Files.write(path, imageBytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveImage(byte[] imageBytes, String savePath) {
        try {
            Path path = Paths.get(savePath);
            Files.createDirectories(path.getParent());
    
            Files.write(path, imageBytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
