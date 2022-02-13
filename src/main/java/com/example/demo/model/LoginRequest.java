package com.example.demo.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    String username;

    @NotNull
    String password;
}
