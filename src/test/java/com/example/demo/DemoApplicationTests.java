package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;

	@Test
	void contextLoads() {

	}

	@Test
	@Transactional
	void test () {
		String username="b@gmail.com";

		User user = userRepository.findByUsername(username);
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		System.out.println(jwtUtils.generateJwtToken(userDetails));
	}

}
