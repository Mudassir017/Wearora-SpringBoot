package com.wearora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wearora.dto.UserAddressRequestDTO;
import com.wearora.dto.UserAddressResponseDTO;
import com.wearora.entity.User;
import com.wearora.entity.UserAddress;
import com.wearora.repository.UserAddressRepository;
import com.wearora.repository.UserRepository;

@Service
public class UserAddressService {

	private final UserAddressRepository addressRepository;
	private final UserRepository userRepository;

	public UserAddressService(UserAddressRepository addressRepository, UserRepository userRepository) {
		this.addressRepository = addressRepository;
		this.userRepository = userRepository;
	}

	private User getUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
	}

	private UserAddressResponseDTO toDTO(UserAddress address) {
		UserAddressResponseDTO dto = new UserAddressResponseDTO();

		dto.setAddressId(address.getId());
		dto.setFullName(address.getFullName());
		dto.setPhone(address.getPhone());
		dto.setAddressLine1(address.getAddressLine1());
		dto.setAddressLine2(address.getAddressLine2());
		dto.setCity(address.getCity());
		dto.setState(address.getState());
		dto.setPostalCode(address.getPostalCode());
		dto.setCountry(address.getCountry());
		dto.setDefault(address.isDefault());
		dto.setCreatedAt(address.getCreatedAt());

		return dto;
	}

	private void copyFields(UserAddressRequestDTO request, UserAddress address) {

		address.setFullName(request.getFullName());
		address.setPhone(request.getPhone());
		address.setAddressLine1(request.getAddressLine1());
		address.setAddressLine2(request.getAddressLine2());
		address.setCity(request.getCity());
		address.setState(request.getState());
		address.setPostalCode(request.getPostalCode());
		address.setCountry(request.getCountry());
	}

	@Transactional(readOnly = true)
	public List<UserAddressResponseDTO> getAddresses(String email) {

		User user = getUser(email);

		return addressRepository.findByUser_IdOrderByIsDefaultDescIdDesc(user.getId()).stream().map(this::toDTO)
				.toList();
	}

	@Transactional
	public UserAddressResponseDTO addAddress(String email, UserAddressRequestDTO request) {

		User user = getUser(email);

		List<UserAddress> existing = addressRepository.findByUser_IdOrderByIsDefaultDescIdDesc(user.getId());

		UserAddress address = new UserAddress();
		address.setUser(user);
		copyFields(request, address);

		boolean makeDefault = request.isDefault() || existing.isEmpty();

		if (makeDefault) {
			existing.forEach(a -> a.setDefault(false));
		}

		address.setDefault(makeDefault);

		return toDTO(addressRepository.save(address));
	}

	@Transactional
	public UserAddressResponseDTO updateAddress(String email, Integer addressId, UserAddressRequestDTO request) {

		User user = getUser(email);

		UserAddress address = addressRepository.findByIdAndUser_Id(addressId, user.getId())
				.orElseThrow(() -> new RuntimeException("Address not found"));

		List<UserAddress> existing = addressRepository.findByUser_IdOrderByIsDefaultDescIdDesc(user.getId());

		copyFields(request, address);

		if (request.isDefault()) {
			existing.forEach(a -> a.setDefault(false));
			address.setDefault(true);
		}

		return toDTO(addressRepository.save(address));
	}

	@Transactional
	public void deleteAddress(String email, Integer addressId) {

		User user = getUser(email);

		UserAddress address = addressRepository.findByIdAndUser_Id(addressId, user.getId())
				.orElseThrow(() -> new RuntimeException("Address not found"));

		boolean wasDefault = address.isDefault();

		addressRepository.delete(address);

		if (wasDefault) {
			List<UserAddress> remaining = addressRepository.findByUser_IdOrderByIsDefaultDescIdDesc(user.getId());

			if (!remaining.isEmpty()) {
				remaining.get(0).setDefault(true);
				addressRepository.save(remaining.get(0));
			}
		}
	}

	@Transactional
	public UserAddressResponseDTO setDefaultAddress(String email, Integer addressId) {

		User user = getUser(email);

		List<UserAddress> existing = addressRepository.findByUser_IdOrderByIsDefaultDescIdDesc(user.getId());

		UserAddress selected = existing.stream().filter(a -> a.getId().equals(addressId)).findFirst()
				.orElseThrow(() -> new RuntimeException("Address not found"));

		existing.forEach(a -> a.setDefault(false));
		selected.setDefault(true);

		addressRepository.saveAll(existing);

		return toDTO(selected);
	}
}