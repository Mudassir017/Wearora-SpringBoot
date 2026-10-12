package com.wearora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.wearora.dto.CheckoutRequestDTO;
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

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock private UserRepository userRepository;
	@Mock private CartRepository cartRepository;
	@Mock private CartItemRepository cartItemRepository;
	@Mock private ProductVariantRepository variantRepository;
	@Mock private OrderRepository orderRepository;
	@Mock private OrderItemRepository orderItemRepository;

	private OrderService service;

	@BeforeEach
	void setUp() {
		service = new OrderService(userRepository, cartRepository, cartItemRepository, variantRepository, orderRepository,
				orderItemRepository);
	}

	@Test
	void checkoutRejectsInsufficientStockBeforeCreatingAnOrder() {
		User user = user(1);
		Cart cart = cart(10);
		ProductVariant variant = variant(100, 1);
		CartItem item = cartItem(variant, 2);

		when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
		when(cartRepository.findWithLockByUserId(1)).thenReturn(Optional.of(cart));
		when(cartItemRepository.findByCart_Id(10)).thenReturn(List.of(item));
		when(variantRepository.findByIdInOrderByIdAsc(List.of(100))).thenReturn(List.of(variant));

		assertThatIllegalArgumentException().isThrownBy(() -> service.checkout("buyer@example.com", checkoutRequest()))
				.withMessage("Insufficient stock for variant: 100");

		verify(orderRepository, never()).save(any());
		verify(orderItemRepository, never()).save(any());
		assertThat(variant.getStockQuantity()).isEqualTo(1);
	}

	@Test
	void getMyOrderDoesNotExposeAnotherUsersOrder() {
		when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user(1)));
		when(orderRepository.findById(77)).thenReturn(Optional.of(order(77, 2, "PLACED")));

		assertThatIllegalArgumentException().isThrownBy(() -> service.getMyOrder("buyer@example.com", 77))
				.withMessage("Order not found");
	}

	@Test
	void cancellationRestoresStockAndMarksOrderCancelled() {
		User user = user(1);
		Order order = order(20, 1, "PLACED");
		ProductVariant variant = variant(100, 3);
		OrderItem item = orderItem(variant, 2);

		when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
		when(orderRepository.findUserOrderWithLock(20, 1)).thenReturn(Optional.of(order));
		when(orderItemRepository.findByOrder_Id(20)).thenReturn(List.of(item));
		when(variantRepository.findByIdInOrderByIdAsc(List.of(100))).thenReturn(List.of(variant));

		service.cancelOrder("buyer@example.com", 20);

		assertThat(variant.getStockQuantity()).isEqualTo(5);
		assertThat(order.getStatus()).isEqualTo("CANCELLED");
		verify(orderRepository).save(order);
	}

	@Test
	void cancelledOrderCannotRestoreStockTwice() {
		when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user(1)));
		when(orderRepository.findUserOrderWithLock(20, 1)).thenReturn(Optional.of(order(20, 1, "CANCELLED")));

		assertThatIllegalArgumentException().isThrownBy(() -> service.cancelOrder("buyer@example.com", 20))
				.withMessage("Order is already cancelled");

		verify(orderItemRepository, never()).findByOrder_Id(any());
		verify(variantRepository, never()).findByIdInOrderByIdAsc(any());
	}

	private CheckoutRequestDTO checkoutRequest() {
		CheckoutRequestDTO request = new CheckoutRequestDTO();
		request.setDeliveryName("Buyer");
		request.setShippingAddress("1 Main Street");
		request.setDeliveryPhone("9999999999");
		return request;
	}

	private User user(int id) {
		User user = new User();
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}

	private Cart cart(int id) {
		Cart cart = new Cart();
		ReflectionTestUtils.setField(cart, "id", id);
		return cart;
	}

	private ProductVariant variant(int id, int stock) {
		ProductVariant variant = new ProductVariant();
		ReflectionTestUtils.setField(variant, "id", id);
		Product product = new Product();
		ReflectionTestUtils.setField(product, "id", 200);
		product.setProductName("Test product");
		variant.setProduct(product);
		variant.setStockQuantity(stock);
		return variant;
	}

	private CartItem cartItem(ProductVariant variant, int quantity) {
		CartItem item = new CartItem();
		item.setVariant(variant);
		item.setQuantity(quantity);
		return item;
	}

	private Order order(int id, int userId, String status) {
		Order order = new Order();
		ReflectionTestUtils.setField(order, "id", id);
		order.setUserId(userId);
		order.setStatus(status);
		return order;
	}

	private OrderItem orderItem(ProductVariant variant, int quantity) {
		OrderItem item = new OrderItem();
		item.setVariant(variant);
		item.setQuantity(quantity);
		item.setUnitPrice(BigDecimal.TEN);
		return item;
	}
}
