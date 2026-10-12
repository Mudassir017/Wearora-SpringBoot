import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./Checkout.css";

const initialForm = {
  deliveryName: "",
  deliveryPhone: "",
  addressLine1: "",
  addressLine2: "",
  city: "",
  state: "",
  postalCode: "",
  country: "India",
};

export default function Checkout() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    ...initialForm,
    deliveryName: user?.name || "",
  });

  const [saveAsDefault, setSaveAsDefault] = useState(false);
  const [defaultAddressId, setDefaultAddressId] = useState(null);
  const [cart, setCart] = useState(null);
  const [loadingCart, setLoadingCart] = useState(true);
  const [loadingAddress, setLoadingAddress] = useState(true);
  const [placingOrder, setPlacingOrder] = useState(false);
  const [error, setError] = useState("");

  async function loadCart() {
    try {
      const response = await api.get("/cart");
      setCart(response.data);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to load your cart. Please try again."
      );
    } finally {
      setLoadingCart(false);
    }
  }

  async function loadDefaultAddress() {
    try {
      const response = await api.get("/addresses");
      const addresses = response.data || [];

      const defaultAddress = addresses.find(
        (address) => address.default === true
      );

      if (defaultAddress) {
        setDefaultAddressId(defaultAddress.addressId);

        setForm({
          deliveryName:
            defaultAddress.fullName || user?.name || "",
          deliveryPhone: defaultAddress.phone || "",
          addressLine1: defaultAddress.addressLine1 || "",
          addressLine2: defaultAddress.addressLine2 || "",
          city: defaultAddress.city || "",
          state: defaultAddress.state || "",
          postalCode: defaultAddress.postalCode || "",
          country: defaultAddress.country || "India",
        });

        setSaveAsDefault(true);
      }
    } catch (err) {
      console.error("Unable to load saved addresses:", err);
    } finally {
      setLoadingAddress(false);
    }
  }

  useEffect(() => {
    if (user) {
      loadCart();
      loadDefaultAddress();
    } else {
      setLoadingCart(false);
      setLoadingAddress(false);
    }
  }, [user]);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  function getFormattedAddress() {
    return [
      form.addressLine1.trim(),
      form.addressLine2.trim(),
      form.city.trim(),
      form.state.trim(),
      form.postalCode.trim(),
      form.country.trim(),
    ]
      .filter(Boolean)
      .join(", ");
  }

  async function saveDefaultAddress() {
    const addressPayload = {
      fullName: form.deliveryName.trim(),
      phone: form.deliveryPhone.trim(),
      addressLine1: form.addressLine1.trim(),
      addressLine2: form.addressLine2.trim(),
      city: form.city.trim(),
      state: form.state.trim(),
      postalCode: form.postalCode.trim(),
      country: form.country.trim(),
      isDefault: true,
    };

    if (defaultAddressId) {
      await api.put(
        `/addresses/${defaultAddressId}`,
        addressPayload
      );
    } else {
      const response = await api.get("/addresses");
      const addresses = response.data || [];

      const existingDefault = addresses.find(
        (address) => address.default === true
      );

      if (existingDefault) {
        await api.put(
          `/addresses/${existingDefault.addressId}`,
          addressPayload
        );
      } else {
        await api.post("/addresses", addressPayload);
      }
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    const requiredFields = [
      ["deliveryName", "Full name"],
      ["deliveryPhone", "Phone number"],
      ["addressLine1", "House number / Street"],
      ["city", "City"],
      ["state", "State"],
      ["postalCode", "PIN code"],
      ["country", "Country"],
    ];

    const missingField = requiredFields.find(
      ([key]) => !form[key].trim()
    );

    if (missingField) {
      setError(`Please enter ${missingField[1]}.`);
      return;
    }

    if (!cart?.items?.length) {
      setError("Your cart is empty.");
      return;
    }

    setPlacingOrder(true);

    try {
      const response = await api.post("/orders/checkout", {
        deliveryName: form.deliveryName.trim(),
        shippingAddress: getFormattedAddress(),
        deliveryPhone: form.deliveryPhone.trim(),
      });

      let addressWarning = "";

      if (saveAsDefault) {
        try {
          await saveDefaultAddress();
        } catch (addressError) {
          console.error(
            "Order placed, but saving the default address failed:",
            addressError
          );

          addressWarning =
            "Your order was placed, but we couldn't update your saved default address. Please check My Addresses.";
        }
      }

      navigate(`/order-success/${response.data.orderId}`, {
        state: {
          order: response.data,
          addressWarning,
        },
        replace: true,
      });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to place your order. Please try again."
      );
    } finally {
      setPlacingOrder(false);
    }
  }

  if (!user) {
    return (
      <main className="checkout-page">
        <h1>Checkout</h1>
        <p>Please log in before placing your order.</p>
        <Link to="/login" className="checkout-button">
          Login to continue
        </Link>
      </main>
    );
  }

  if (loadingCart || loadingAddress) {
    return (
      <main className="checkout-page">
        Loading checkout...
      </main>
    );
  }

  if (!cart?.items?.length) {
    return (
      <main className="checkout-page">
        <h1>Your bag is empty</h1>
        <p>Add something you love before checking out.</p>
        <Link to="/products" className="checkout-button">
          Continue shopping
        </Link>
      </main>
    );
  }

  const money = (value) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 2,
    }).format(Number(value || 0));

  return (
    <main className="checkout-page">
      <div className="checkout-heading">
        <p className="checkout-eyebrow">WEARORA / CHECKOUT</p>
        <h1>Almost yours.</h1>
        <p>
          Confirm your delivery details to place your order.
        </p>
      </div>

      <div className="checkout-layout">
        <form className="checkout-form" onSubmit={handleSubmit}>
          <section className="checkout-section">
            <h2>Delivery details</h2>

            {defaultAddressId && (
              <p className="checkout-saved-address-note">
                Your default delivery address has been filled in.
              </p>
            )}

            <label htmlFor="deliveryName">Full name</label>
            <input
              id="deliveryName"
              name="deliveryName"
              value={form.deliveryName}
              onChange={handleChange}
              maxLength={100}
              required
              autoComplete="name"
            />

            <label htmlFor="deliveryPhone">Phone number</label>
            <input
              id="deliveryPhone"
              name="deliveryPhone"
              type="tel"
              value={form.deliveryPhone}
              onChange={handleChange}
              maxLength={15}
              required
              autoComplete="tel"
            />

            <label htmlFor="addressLine1">
              House number / Street / Area
            </label>
            <input
              id="addressLine1"
              name="addressLine1"
              value={form.addressLine1}
              onChange={handleChange}
              maxLength={255}
              required
              autoComplete="address-line1"
            />

            <label htmlFor="addressLine2">
              Landmark / Apartment (optional)
            </label>
            <input
              id="addressLine2"
              name="addressLine2"
              value={form.addressLine2}
              onChange={handleChange}
              maxLength={255}
              autoComplete="address-line2"
            />

            <label htmlFor="city">City</label>
            <input
              id="city"
              name="city"
              value={form.city}
              onChange={handleChange}
              maxLength={100}
              required
              autoComplete="address-level2"
            />

            <label htmlFor="state">State</label>
            <input
              id="state"
              name="state"
              value={form.state}
              onChange={handleChange}
              maxLength={100}
              required
              autoComplete="address-level1"
            />

            <label htmlFor="postalCode">PIN code</label>
            <input
              id="postalCode"
              name="postalCode"
              value={form.postalCode}
              onChange={handleChange}
              maxLength={20}
              required
              inputMode="numeric"
              autoComplete="postal-code"
            />

            <label htmlFor="country">Country</label>
            <input
              id="country"
              name="country"
              value={form.country}
              onChange={handleChange}
              maxLength={100}
              required
              autoComplete="country-name"
            />

            <label className="checkout-default-address">
              <input
                type="checkbox"
                checked={saveAsDefault}
                onChange={(event) =>
                  setSaveAsDefault(event.target.checked)
                }
              />

              <span>
                <strong>Set as default address</strong>
                <small>
                  Save this address for your next order
                </small>
              </span>
            </label>
          </section>

          <section className="checkout-section">
            <h2>Payment method</h2>

            <div className="checkout-payment">
              <span className="checkout-payment-radio">●</span>

              <div>
                <strong>Cash on Delivery</strong>
                <p>Pay when your order arrives.</p>
              </div>

              <span className="checkout-cod">COD</span>
            </div>
          </section>

          {error && (
            <p className="checkout-error" role="alert">
              {error}
            </p>
          )}

          <button
            type="submit"
            className="checkout-button"
            disabled={placingOrder}
          >
            {placingOrder ? "Placing order..." : "Place order"}
          </button>

          <Link to="/cart" className="checkout-back">
            ← Return to bag
          </Link>
        </form>

        <aside className="checkout-summary">
          <h2>Order summary</h2>

          {cart.items.map((item) => (
            <div
              className="checkout-item"
              key={item.cartItemId}
            >
              <div className="checkout-item-info">
                <strong>{item.productName}</strong>
                <span>
                  Size: {item.size} · Qty: {item.quantity}
                </span>
              </div>

              <span>{money(item.subtotal)}</span>
            </div>
          ))}

          <div className="checkout-total">
            <span>Total</span>
            <strong>{money(cart.total)}</strong>
          </div>

          <p className="checkout-summary-note">
            Payment is collected upon delivery. Your order total
            is calculated again by the server when the order is placed.
          </p>
        </aside>
      </div>
    </main>
  );
}