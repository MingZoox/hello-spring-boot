package com.example.demo.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UsernamePasswordRequest {
    @Email (message = "Username is email")
    @NotBlank(message = "Username is mandatory")
    String username;

    @NotBlank(message = "Password is mandatory")
    String password;
}
