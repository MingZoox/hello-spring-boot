package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UserController {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping(value = "/user")
    public UserDTO userPage() {
        UserDTO userDTO = userDetailsService.getCurrentUserInformation();
        return userDTO;
    }

    @PostMapping(path = "/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        userDetailsService.updateAvatar(file);
        return "upload avatar success";
    }
}
