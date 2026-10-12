
package com.wearora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wearora.security.JwtAuthenticationFilter;
import com.wearora.security.JwtService;

@Configuration
public class SecurityConfig {

	private final JwtService jwtService;

	public SecurityConfig(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> {
		}).csrf(csrf -> csrf.disable()).formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth

						// Login and registration are public
						.requestMatchers("/api/auth/**").permitAll()

						// Allow browser CORS preflight requests
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// Public product and category reads
						.requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()

						// Only ADMIN can manage products
						.requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

						// All other endpoints require authentication
						.anyRequest().authenticated())

				.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
