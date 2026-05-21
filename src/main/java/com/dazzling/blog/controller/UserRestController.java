package com.dazzling.blog.controller;

import com.dazzling.blog.exception.ResourceNotFoundException;
import com.dazzling.blog.models.Comment;
import com.dazzling.blog.models.User;
import com.dazzling.blog.repositories.CommentRepository;
import com.dazzling.blog.repositories.UserRepository;
import com.dazzling.blog.service.ImageDownloadService;
import com.dazzling.blog.service.ImageService;
import com.dazzling.blog.utils.SlugUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDownloadService imageDownloadService;

    @Value("${file.upload.avatarPath}")
    private String avatarPath;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping(params = "v")
    public User getBySlug(@RequestParam(value = "{v}") String slug) {
        return userRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("未找到用户！"));
    }

    @PostMapping
    public ResponseEntity<?> newUser(
        @RequestBody Map<String, String> payload
    ) {
        String type = payload.get("type");
        String user = payload.get("user");
        String email = payload.get("email");
        String password = payload.get("password");

        if (userRepository.findByUsername(user) != null) {
            return ResponseEntity.ok(Map.of(
                "status", HttpStatus.NOT_ACCEPTABLE,
                "message", "添加用户 「" + user + "」 失败！"
            ));
        }

        String slug = SlugUtil.randomSlug();
        
        String avatar = user + ".svg";

        String avatarRemoteUrl = imageService.generateAvatarUrl(user);

        imageDownloadService.downloadAndSave(avatarRemoteUrl, avatarPath + avatar);

        if (type == "guest") {
            userRepository.save(new User(slug, user, email, "", avatar, "ROLE_GUEST"));
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "添加成功！"
            ));

        } else {
            userRepository.save(new User(slug, user, email, password, avatar, "ROLE_USER"));
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "添加成功！"
            ));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUserInformation(
        @RequestBody Map<String, Object> payload
    ) {
        Long id = ((Long) payload.get("id")).longValue();
        User user = userRepository.findById(id).orElseThrow();

        if (payload.get("avatar") != null)
            user.setAvatar((String) payload.get("avatar"));

        if (payload.get("email") != null)
            user.setEmail((String) payload.get("eamil"));

        if (payload.get("password") != null)
            user.setPassword(passwordEncoder.encode((String) payload.get("password")));

        if (payload.get("username") != null)
            user.setUsername((String) payload.get("username"));
        
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK,
                "message", "修改成功！"
                ));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(
        @RequestBody Map<String, Object> payload
    ) {
        Long id = ((Number) payload.get("id")).longValue();
        List<Comment> comments = commentRepository.findByUserId(id);
        for (Comment comment : comments) {
            commentRepository.deleteById(comment.getId().longValue());
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK,
                "message", "删除成功！"
                ));
    }

    @GetMapping("/session")
    public ResponseEntity<?> userSession(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED,
                            "error", "未登录！"
                    ));
        }
        User user = userRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("用户 「" + principal.getName() + "」 不存在！"));
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK,
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }
}