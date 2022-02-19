package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.model.UsernamePasswordRequest;
import com.example.demo.security.JwtUtils;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class WebController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    EmailService emailService;

    @GetMapping("/")
    public String homePage() {
        return "homepage";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @PostMapping("/login")
    public String authenticateUser(@Valid @RequestBody UsernamePasswordRequest loginRequest) {

        // Xác thực từ username và password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Trả về jwt cho người dùng.
        return jwt;
    }

    @PostMapping("/registration")
    public String registrationUser(@Valid @RequestBody UsernamePasswordRequest registrationRequest) {
        try {
            User newUser = userDetailsService.registrationUser(
                    registrationRequest.getUsername(),
                    registrationRequest.getPassword()
            );
            emailService.sendVerificationEmail(newUser);
        } catch (DataIntegrityViolationException e) {
            return "user already exist";
        }
        return "success";
    }

    @GetMapping("/registration-confirm")
    public String registrationConfirm(@RequestParam("token") String token) {
        if (emailService.verifiedTokenEmail(token)) {
            return "verified success";
        }
        return "verified fail";
    }

    @GetMapping("/registration-resend")
    public String resendTokenEmail(@RequestParam("username") String username) {
        emailService.resendVerificationEmail(username);
        return "success";
    }
}