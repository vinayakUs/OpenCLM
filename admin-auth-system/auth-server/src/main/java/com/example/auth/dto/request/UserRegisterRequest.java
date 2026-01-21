package com.example.auth.dto.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    public  String email;
    public String password;
    public String confirmPassword;
    public String name;
}
