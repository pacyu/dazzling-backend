package com.dazzling.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String username;
    private String role;
    private String message;
}
