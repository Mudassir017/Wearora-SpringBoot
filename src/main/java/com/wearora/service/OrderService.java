package com.wearora.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wearora.dto.CheckoutRequestDTO;
import com.wearora.dto.OrderItemResponseDTO;
import com.wearora.dto.OrderResponseDTO;
import com.wearora.entity.Cart;
import com.wearora.entity.CartItem;
import com.wearora.entity.Order;
import com.wearora.entity.OrderItem;
import com.wearora.entity.Product;
import com.wearora.entity.ProductVariant;
import com.wearora.entity.User;
import com.wearora.repository.CartItemRepository;
import com.wearora.repository.CartRepository;
import com.wearora.repository.OrderItemRepository;
import com.wearora.repository.OrderRepository;
import com.wearora.repository.ProductVariantRepository;
import com.wearora.repository.UserRepository;

@Service
public class OrderService {

	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductVariantRepository variantRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;

	public OrderService(UserRepository userRepository, CartRepository cartRepository,
			CartItemRepository cartItemRepository, ProductVariantRepository variantRepository,
			OrderRepository orderRepository, OrderItemRepository orderItemRepository) {

		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.variantRepository = variantRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}

	@Transactional
	public OrderResponseDTO checkout(String email, CheckoutRequestDTO request) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// Serializes checkout attempts for one cart, so its items cannot produce two orders.
		Cart cart = cartRepository.findWithLockByUserId(user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

		List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());

		if (cartItems.isEmpty()) {
			throw new IllegalArgumentException("Cart is empty");
		}

		Map<Integer, Integer> requestedQuantities = cartItems.stream().collect(java.util.stream.Collectors
				.toMap(item -> item.getVariant().getId(), CartItem::getQuantity, Integer::sum, LinkedHashMap::new));
		Map<Integer, ProductVariant> lockedVariants = variantRepository
				.findByIdInOrderByIdAsc(requestedQuantities.keySet().stream().sorted().toList()).stream()
				.collect(java.util.stream.Collectors.toMap(ProductVariant::getId, variant -> variant));

		if (lockedVariants.size() != requestedQuantities.size()) {
			throw new IllegalArgumentException("Product variant not found");
		}

		for (Map.Entry<Integer, Integer> requestedQuantity : requestedQuantities.entrySet()) {
			ProductVariant variant = lockedVariants.get(requestedQuantity.getKey());
			if (requestedQuantity.getValue() > variant.getStockQuantity()) {
				throw new IllegalArgumentException("Insufficient stock for variant: " + variant.getId());
			}
		}

		Order order = new Order();
		order.setUserId(user.getId());
		order.setDeliveryName(request.getDeliveryName());
		order.setShippingAddress(request.getShippingAddress().trim());
		order.setDeliveryPhone(request.getDeliveryPhone().trim());
		order.setPaymentMethod("COD");
		order.setPaymentStatus("PENDING");
		order.setStatus("PLACED");
		order.setTotalAmount(BigDecimal.ZERO);

		order = orderRepository.save(order);

		BigDecimal total = BigDecimal.ZERO;

		for (CartItem item : cartItems) {
			ProductVariant variant = lockedVariants.get(item.getVariant().getId());

			Product product = variant.getProduct();

			BigDecimal price = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();

			BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setVariant(variant);
			orderItem.setQuantity(item.getQuantity());
			orderItem.setUnitPrice(price);

			orderItemRepository.save(orderItem);

			variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());

			total = total.add(subtotal);
		}

		order.setTotalAmount(total);
		orderRepository.save(order);

		cartItemRepository.deleteAll(cartItems);

		return buildResponse(order);
	}

	@Transactional(readOnly = true)
	public List<OrderResponseDTO> getMyOrders(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return orderRepository.findByUserIdOrderByIdDesc(user.getId()).stream().map(this::buildResponse).toList();
	}

	@Transactional(readOnly = true)
	public OrderResponseDTO getMyOrder(String email, Integer orderId) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		Order order = orderRepository.findById(orderId).filter(o -> o.getUserId().equals(user.getId()))
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));

		return buildResponse(order);
	}

	@Transactional
	public OrderResponseDTO cancelOrder(String email, Integer orderId) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		Order order = orderRepository.findUserOrderWithLock(orderId, user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Order not found"));

		if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
			throw new IllegalArgumentException("Order is already cancelled");
		}

		if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
			throw new IllegalArgumentException("Delivered order cannot be cancelled");
		}

		if (!"PLACED".equalsIgnoreCase(order.getStatus()) && !"CONFIRMED".equalsIgnoreCase(order.getStatus())
				&& !"PROCESSING".equalsIgnoreCase(order.getStatus())) {
			throw new IllegalArgumentException("Order cannot be cancelled in its current status");
		}

		List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
		Map<Integer, Integer> restoreQuantities = items.stream().collect(java.util.stream.Collectors
				.toMap(item -> item.getVariant().getId(), OrderItem::getQuantity, Integer::sum, LinkedHashMap::new));
		Map<Integer, ProductVariant> lockedVariants = variantRepository
				.findByIdInOrderByIdAsc(restoreQuantities.keySet().stream().sorted().toList()).stream()
				.collect(java.util.stream.Collectors.toMap(ProductVariant::getId, variant -> variant));

		if (lockedVariants.size() != restoreQuantities.size()) {
			throw new IllegalArgumentException("Variant not found");
		}

		for (Map.Entry<Integer, Integer> restore : restoreQuantities.entrySet()) {
			ProductVariant variant = lockedVariants.get(restore.getKey());
			variant.setStockQuantity(variant.getStockQuantity() + restore.getValue());
		}

		order.setStatus("CANCELLED");
		orderRepository.save(order);

		return buildResponse(order);
	}

	private OrderResponseDTO buildResponse(Order order) {

		List<OrderItemResponseDTO> items = orderItemRepository.findByOrder_Id(order.getId()).stream().map(item -> {
			ProductVariant variant = item.getVariant();
			Product product = variant.getProduct();

			return new OrderItemResponseDTO(item.getId(), variant.getId(), product.getId(), product.getProductName(),
					variant.getSize(), item.getQuantity(), item.getUnitPrice());
		}).toList();

		return new OrderResponseDTO(order.getId(), order.getDeliveryName(), order.getShippingAddress(),
				order.getDeliveryPhone(), order.getTotalAmount(), order.getStatus(), order.getPaymentMethod(),
				order.getPaymentStatus(), order.getCreatedAt(), items);
	}
}
