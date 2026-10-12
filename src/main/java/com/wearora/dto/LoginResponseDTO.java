
package com.wearora.dto;

public class LoginResponseDTO {

	private final String token;
	private final String tokenType = "Bearer";
	private final Integer userId;
	private final String name;
	private final String email;

	public LoginResponseDTO(String token, Integer userId, String name, String email) {
		this.token = token;
		this.userId = userId;
		this.name = name;
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
