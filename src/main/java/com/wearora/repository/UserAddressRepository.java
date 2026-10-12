package com.wearora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {

	List<UserAddress> findByUser_IdOrderByIsDefaultDescIdDesc(Integer userId);

	Optional<UserAddress> findByIdAndUser_Id(Integer addressId, Integer userId);

	boolean existsByUser_IdAndIsDefaultTrue(Integer userId);
}