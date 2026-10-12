
package com.wearora.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wearora.dto.LoginRequestDTO;
import com.wearora.dto.LoginResponseDTO;
import com.wearora.dto.RegisterRequestDTO;
import com.wearora.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public String register(@Valid @RequestBody RegisterRequestDTO request) {
		authService.register(request);
		return "Registration successful";
	}

	@PostMapping("/login")
	public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
		return authService.login(request);
	}
}
