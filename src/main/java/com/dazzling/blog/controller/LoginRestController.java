package com.dazzling.blog.controller;

import com.dazzling.blog.dto.LoginRequest;
import com.dazzling.blog.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping()
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        // 将 SecurityContext 保存到 Session 中（确保后续请求能识别）
        SecurityContext securityContext = SecurityContextHolder.getContext();
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return ResponseEntity.ok(new LoginResponse(username, role, "登录成功！"));
    }
}
