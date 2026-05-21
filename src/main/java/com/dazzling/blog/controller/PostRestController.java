package com.dazzling.blog.controller;

import com.dazzling.blog.models.User;
import com.dazzling.blog.repositories.UserRepository;
import com.dazzling.blog.service.SmtpService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.mail.MessagingException;
import java.util.*;

@RestController
@RequestMapping("/api/feedback")
public class PostRestController {

    @Autowired
    private SmtpService smtpService;
    @Autowired
    private UserRepository userRepository;

    @Value("${blog.notify.email}")
    private String notifyEmail;

    @Value("${blog.notify.name}")
    private String notifyName;

    @PostMapping()
    public ResponseEntity<?> send(@RequestBody Map<String, String> payload,
                                  final Locale locale) {
        String senderEmail = payload.get("senderEmail");
        String content = payload.get("content");
        User user = userRepository.findByEmail(senderEmail);

        try {
            if (user != null) {
                smtpService.sendTextMail(user.getUsername(), senderEmail, "反馈", content,
                        notifyName, notifyEmail, locale);

                return ResponseEntity.ok(Map.of(
                        "status", 200,
                        "message", "邮件发送成功!",
                        "content", content
                ));
            } else if (!Objects.equals(senderEmail, "")) {
                userRepository.save(new User(senderEmail));
                smtpService.sendTextMail("", senderEmail, "反馈", content,
                        notifyName, notifyEmail, locale);
                return ResponseEntity.ok(Map.of(
                        "status", 200,
                        "message", "邮件发送成功!",
                        "content", content
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", 400,
                        "message", "邮件发送失败!",
                        "content", "邮件为空!"
                ));
            }
        } catch (MessagingException e) {
            return ResponseEntity.ok(Map.of(
                    "status", 400,
                    "message", e.getMessage() + "邮件发送失败!",
                    "content", content
            ));
        }
    }
}