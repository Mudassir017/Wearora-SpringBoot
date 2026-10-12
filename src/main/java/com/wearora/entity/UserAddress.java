package com.wearora.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_addresses")
public class UserAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "addressId")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	@Column(name = "fullName", nullable = false, length = 100)
	private String fullName;

	@Column(name = "phone", nullable = false, length = 15)
	private String phone;

	@Column(name = "addressLine1", nullable = false, length = 255)
	private String addressLine1;

	@Column(name = "addressLine2", length = 255)
	private String addressLine2;

	@Column(name = "city", nullable = false, length = 100)
	private String city;

	@Column(name = "state", nullable = false, length = 100)
	private String state;

	@Column(name = "postalCode", nullable = false, length = 20)
	private String postalCode;

	@Column(name = "country", nullable = false, length = 100)
	private String country;

	@Column(name = "isDefault", nullable = false)
	private boolean isDefault = false;

	@Column(name = "createdAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public UserAddress() {
	}

	public Integer getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPhone() {
		return phone;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCountry() {
		return country;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}