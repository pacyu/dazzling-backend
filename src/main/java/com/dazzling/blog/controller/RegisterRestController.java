package com.dazzling.blog.controller;

import com.dazzling.blog.dto.RegisterRequest;
import com.dazzling.blog.dto.RegisterResponse;
import com.dazzling.blog.models.User;
import com.dazzling.blog.repositories.UserRepository;
import com.dazzling.blog.service.ImageDownloadService;
import com.dazzling.blog.service.ImageService;
import com.dazzling.blog.utils.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/register")
public class RegisterRestController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDownloadService imageDownloadService;

    @Value("${file.upload.path}")
    private String avatarPath;

    @PostMapping
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.badRequest().body(
                    new RegisterResponse(request.getUsername(), "ROLE_USER", "用户名已存在！"));
        }
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body(
                    new RegisterResponse(request.getUsername(), "ROLE_USER", "邮箱已存在！"));
        }

        String username = request.getUsername();

        String avatar = username + ".svg";

        String avatarRemoteUrl = imageService.generateAvatarUrl(username);

        imageDownloadService.downloadAndSave(avatarRemoteUrl, avatarPath + avatar);

        User user = new User(
            SlugUtil.randomSlug(12),
            request.getEmail(),
            username,
            passwordEncoder.encode(request.getPassword()),
            avatar,
            "ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.ok(
                new RegisterResponse(request.getUsername(), "ROLE_USER", "注册成功！"));
    }
}
