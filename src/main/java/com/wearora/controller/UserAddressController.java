package com.wearora.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wearora.dto.UserAddressRequestDTO;
import com.wearora.dto.UserAddressResponseDTO;
import com.wearora.service.UserAddressService;

@RestController
@RequestMapping("/api/addresses")
public class UserAddressController {

	private final UserAddressService addressService;

	public UserAddressController(UserAddressService addressService) {
		this.addressService = addressService;
	}

	@GetMapping
	public ResponseEntity<List<UserAddressResponseDTO>> getAddresses(Principal principal) {

		return ResponseEntity.ok(addressService.getAddresses(principal.getName()));
	}

	@PostMapping
	public ResponseEntity<UserAddressResponseDTO> addAddress(Principal principal,
			@RequestBody UserAddressRequestDTO request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(principal.getName(), request));
	}

	@PutMapping("/{addressId}")
	public ResponseEntity<UserAddressResponseDTO> updateAddress(Principal principal, @PathVariable Integer addressId,
			@RequestBody UserAddressRequestDTO request) {

		return ResponseEntity.ok(addressService.updateAddress(principal.getName(), addressId, request));
	}

	@DeleteMapping("/{addressId}")
	public ResponseEntity<Void> deleteAddress(Principal principal, @PathVariable Integer addressId) {

		addressService.deleteAddress(principal.getName(), addressId);

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{addressId}/default")
	public ResponseEntity<UserAddressResponseDTO> setDefaultAddress(Principal principal,
			@PathVariable Integer addressId) {

		return ResponseEntity.ok(addressService.setDefaultAddress(principal.getName(), addressId));
	}
}