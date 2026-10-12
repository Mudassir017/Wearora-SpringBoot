package com.wearora.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wearora.dto.CheckoutRequestDTO;
import com.wearora.dto.OrderResponseDTO;
import com.wearora.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/checkout")
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponseDTO checkout(Principal principal, @Valid @RequestBody CheckoutRequestDTO request) {

		return orderService.checkout(principal.getName(), request);
	}

	@GetMapping
	public List<OrderResponseDTO> getMyOrders(Principal principal) {
		return orderService.getMyOrders(principal.getName());
	}

	@GetMapping("/{orderId}")
	public OrderResponseDTO getMyOrder(Principal principal, @PathVariable @Min(1) Integer orderId) {

		return orderService.getMyOrder(principal.getName(), orderId);
	}

	@PatchMapping("/{orderId}/cancel")
	public OrderResponseDTO cancelOrder(Principal principal, @PathVariable @Min(1) Integer orderId) {

		return orderService.cancelOrder(principal.getName(), orderId);
	}
}