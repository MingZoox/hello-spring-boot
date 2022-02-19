package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
public class UserController {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping(value = "/user-avatar/{username}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] userAvatar(@PathVariable String username) {
        return userDetailsService.getAvatarByUsername(username);
    }

    @GetMapping(value = "/user")
    public UserDTO userPage() {
        UserDTO userDTO = userDetailsService.getCurrentUserInformation();

        String avatarUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/user-avatar/").path(userDTO.getUsername())
                .toUriString();

        userDTO.setAvatarUri(avatarUri);
        return userDTO;
    }

    @PostMapping("/upload/{username}")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @PathVariable String username) throws IOException {
        userDetailsService.updateAvatarByUsername(file, username);
        return "success";
    }
}
