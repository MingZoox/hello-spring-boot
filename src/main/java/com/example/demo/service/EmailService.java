package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;

@Service
@Transactional
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendVerificationEmail(User newUser) {
        String token = jwtUtils.generateJwtToken(new UserDetailsImpl(newUser));
        String subject = "Registration Confirmation";
        String uriVerified = ServletUriComponentsBuilder
                .fromCurrentContextPath().toUriString();
        uriVerified = uriVerified + "/registration-confirm?token=" + token;
        String text = "Please confirm your email !! \n" + uriVerified;
        sendSimpleMessage(newUser.getUsername(), subject, text);
    }

    public boolean verifiedTokenEmail(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            userRepository.enableUser(username);
            return true;
        }
        return false;
    }

    public void resendVerificationEmail (String username) {
        User user = userRepository.findByUsername(username);
        sendVerificationEmail(user);
    }
}
