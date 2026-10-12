package com.wearora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CheckoutRequestDTO {

	@NotBlank
	@Size(max = 100)
	private String deliveryName;

	@NotBlank
	@Size(max = 1000)
	private String shippingAddress;

	@NotBlank
	@Size(max = 15)
	private String deliveryPhone;

	public String getDeliveryName() {
		return deliveryName;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public String getDeliveryPhone() {
		return deliveryPhone;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }
}