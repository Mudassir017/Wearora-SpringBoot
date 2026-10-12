
import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, Minus, Plus, ShoppingBag, Trash2 } from "lucide-react";
import api from "../api/api";
import { useAuth } from "../context/AuthContext";
import "./Cart.css";

function formatPrice(value) {
  return `₹${Number(value ?? 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

export default function Cart() {
  const { user } = useAuth();

  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [busyItem, setBusyItem] = useState(null);
  const [error, setError] = useState("");

  const fetchCart = useCallback(async () => {
    setError("");

    try {
      const response = await api.get("/cart");
      setCart(response.data);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to load your cart. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (user) {
      fetchCart();
    } else {
      setCart(null);
      setLoading(false);
    }
  }, [user, fetchCart]);

  async function updateQuantity(item, quantity) {
    if (quantity < 1 || busyItem !== null) return;

    setBusyItem(item.cartItemId);
    setError("");

    try {
      await api.put(`/cart/items/${item.cartItemId}`, null, {
        params: { quantity },
      });

      await fetchCart();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to update quantity. Please try again."
      );
    } finally {
      setBusyItem(null);
    }
  }

  async function removeItem(item) {
    setBusyItem(item.cartItemId);
    setError("");

    try {
      await api.delete(`/cart/items/${item.cartItemId}`);
      await fetchCart();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to remove this item. Please try again."
      );
    } finally {
      setBusyItem(null);
    }
  }

  if (loading) {
    return (
      <main className="cart-state">
        <p>Loading your cart...</p>
      </main>
    );
  }

  if (!user) {
    return (
      <main className="cart-state">
        <ShoppingBag size={38} strokeWidth={1.3} />
        <h1>Sign in to view your cart</h1>
        <p>Your selected pieces will be waiting for you.</p>
        <Link className="cart-primary-button" to="/login">
          Sign In
        </Link>
      </main>
    );
  }

  const items = cart?.items ?? [];

  return (
    <main className="cart-page">
      <div className="cart-heading">
        <p className="cart-eyebrow">YOUR SELECTION</p>
        <h1>Shopping Bag</h1>
        <p>{items.length} {items.length === 1 ? "item" : "items"}</p>
      </div>

      {error && (
        <div className="cart-error" role="alert">
          {error}
          <button type="button" onClick={fetchCart}>
            Retry
          </button>
        </div>
      )}

      {items.length === 0 ? (
        <section className="cart-empty">
          <ShoppingBag size={44} strokeWidth={1.2} />
          <h2>Your bag is waiting.</h2>
          <p>Explore the collection and find something you love.</p>
          <Link to="/products" className="cart-primary-button">
            Continue Shopping
          </Link>
        </section>
      ) : (
        <section className="cart-layout">
          <div className="cart-items">
            {items.map((item) => (
              <article className="cart-item" key={item.cartItemId}>
                <div className="cart-item-image">
                  <ShoppingBag size={30} strokeWidth={1.2} />
                </div>

                <div className="cart-item-info">
                  <Link
                    to={`/products/${item.productId}`}
                    className="cart-item-name"
                  >
                    {item.productName}
                  </Link>

                  <p className="cart-item-size">Size: {item.size}</p>
                  <p className="cart-item-price">
                    {formatPrice(item.price)}
                  </p>

                  <div className="cart-item-actions">
                    <div className="cart-quantity">
                      <button
                        type="button"
                        aria-label="Decrease quantity"
                        disabled={
                          busyItem !== null || item.quantity <= 1
                        }
                        onClick={() =>
                          updateQuantity(item, item.quantity - 1)
                        }
                      >
                        <Minus size={14} />
                      </button>

                      <span>
                        {busyItem === item.cartItemId
                          ? "..."
                          : item.quantity}
                      </span>

                      <button
                        type="button"
                        aria-label="Increase quantity"
                        disabled={busyItem !== null}
                        onClick={() =>
                          updateQuantity(item, item.quantity + 1)
                        }
                      >
                        <Plus size={14} />
                      </button>
                    </div>

                    <button
                      type="button"
                      className="cart-remove-button"
                      disabled={busyItem !== null}
                      onClick={() => removeItem(item)}
                    >
                      <Trash2 size={15} />
                      Remove
                    </button>
                  </div>
                </div>

                <p className="cart-item-subtotal">
                  {formatPrice(item.subtotal)}
                </p>
              </article>
            ))}

            <Link to="/products" className="cart-continue-link">
              <ArrowLeft size={16} />
              Continue shopping
            </Link>
          </div>

          <aside className="cart-summary">
            <h2>Order Summary</h2>

            <div className="cart-summary-row">
              <span>Subtotal</span>
              <span>{formatPrice(cart?.total)}</span>
            </div>

            <div className="cart-summary-row">
              <span>Shipping</span>
              <span>Calculated at checkout</span>
            </div>

            <div className="cart-summary-total">
              <span>Total</span>
              <strong>{formatPrice(cart?.total)}</strong>
            </div>

			<Link to="/checkout" className="checkout-button">
			  Proceed to checkout
			</Link>

            <p className="cart-summary-note">
              Final shipping charges will be confirmed at checkout.
            </p>
          </aside>
        </section>
      )}
    </main>
  );
}
