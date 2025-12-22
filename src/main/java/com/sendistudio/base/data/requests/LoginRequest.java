package com.sendistudio.base.data.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}