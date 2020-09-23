package com.myclass.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myclass.dto.LoginDto;
import com.myclass.entity.User;
import com.myclass.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("")
	public Object post(@RequestBody User user) {
		try {
			userService.add(user);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("login")
	public Object login(@RequestBody LoginDto loginDto) {
		try {
			// Kiểm tra đăng nhập (email và mật khẩu)
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
			
			// Nếu không xảy ra exception tức là thông tin hợp lệ
	        // Set thông tin authentication vào Security Context
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			Date dateNow = new Date();
			// Tạo token
			String token = Jwts.builder()
					.setSubject(loginDto.getEmail()) // Lưu email
					.setIssuedAt(dateNow) // Ngày tạo
					.setExpiration(new Date(dateNow.getTime() + 864000000L)) // Ngày hết hạn
					.signWith(SignatureAlgorithm.HS512, "ALO123") // Mã hóa thông tin
					.compact();
			
			// Trả về token cho người dùng.
			return new ResponseEntity<String>(token, HttpStatus.OK);
		}
		catch (BadCredentialsException e) {
			// Nếu không xảy ra exception tức là thông tin không hợp lệ
			return new ResponseEntity<String>("Sai thông tin đăng nhập!", HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}
	}
}
