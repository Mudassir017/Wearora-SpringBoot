
package com.wearora.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wearora.dto.LoginRequestDTO;
import com.wearora.dto.LoginResponseDTO;
import com.wearora.dto.RegisterRequestDTO;
import com.wearora.entity.User;
import com.wearora.repository.UserRepository;
import com.wearora.security.JwtService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public void register(RegisterRequestDTO request) {
		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
		String phone = request.getPhone().trim();

		if (userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email is already registered");
		}

		if (userRepository.existsByPhone(phone)) {
			throw new IllegalArgumentException("Phone is already registered");
		}

		User user = new User();
		user.setName(request.getName().trim());
		user.setEmail(email);
		user.setPhone(phone);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("CUSTOMER");

		userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public LoginResponseDTO login(LoginRequestDTO request) {
		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Invalid email or password");
		}

		String token = jwtService.generateToken(user.getEmail(), user.getRole());

		return new LoginResponseDTO(token, user.getId(), user.getName(), user.getEmail());
	}
}
