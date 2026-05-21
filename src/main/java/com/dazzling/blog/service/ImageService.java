package com.dazzling.blog.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ImageService {

    @Value("${pollinations.ai.key}")
    private String pollenKey;

  public String generateAvatarUrl(String userSeed) {
      // 增加 size 参数指定图片大小为 256x256 像素
      String avatarUrl = String.format(
          "https://api.dicebear.com/9.x/shape/svg?seed=%s&size=256", 
          userSeed
      );
      return avatarUrl;
  }

  public byte[] generateCover(String prompt) {
    String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8)
        .replace("+", "%20");
    String apiUrl = "https://gen.pollinations.ai/image/" + encodedPrompt;

    Map<String, String> requestParams = new HashMap<>();
    requestParams.put("width", "1024");
    requestParams.put("height", "768");
    requestParams.put("model", "zimage"); // 可选: 'turbo', 'realistic', 'anime' 等
    requestParams.put("quality", "high");
    requestParams.put("seed", "1");
    requestParams.put("negative_prompt", "worst%20quality%2C%20blurry");

    String paramString = requestParams.entrySet().stream()
        .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
        .collect(Collectors.joining("&"));
    String finalUrl = apiUrl + (paramString.isEmpty() ? "" : "?" + paramString);

    HttpRequest request = HttpRequest.newBuilder(URI.create(finalUrl))
    .header("Authorization", "Bearer " + pollenKey)
    .GET()
    .build();

    try {
        HttpResponse<byte[]> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
        int status = response.statusCode();
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        if (status == 200 && contentType.startsWith("image/")) {
            return response.body();
        } else {
            String errorMsg = new String(response.body(), StandardCharsets.UTF_8);
            System.err.println("API 错误: " + errorMsg);
            return null;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
  }
}
