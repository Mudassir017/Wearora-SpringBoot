
package com.wearora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
}
