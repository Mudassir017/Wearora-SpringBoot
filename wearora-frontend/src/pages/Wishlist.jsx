
import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Heart, ShoppingBag, Trash2 } from "lucide-react";
import api from "../api/api";
import { useAuth } from "../context/AuthContext";
import "./Wishlist.css";

function formatPrice(value) {
  return `₹${Number(value ?? 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

export default function Wishlist() {
  const { user } = useAuth();

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [removingId, setRemovingId] = useState(null);
  const [error, setError] = useState("");

  const fetchWishlist = useCallback(async () => {
    setError("");

    try {
      const response = await api.get("/wishlist");
      setItems(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to load your wishlist. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (user) {
      fetchWishlist();
    } else {
      setItems([]);
      setLoading(false);
    }
  }, [user, fetchWishlist]);

  async function removeItem(item) {
    setRemovingId(item.wishlistItemId);
    setError("");

    try {
      await api.delete(`/wishlist/items/${item.wishlistItemId}`);

      setItems((current) =>
        current.filter(
          (saved) => saved.wishlistItemId !== item.wishlistItemId
        )
      );
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to remove this product. Please try again."
      );
    } finally {
      setRemovingId(null);
    }
  }

  if (loading) {
    return (
      <main className="wishlist-state">
        Loading your wishlist...
      </main>
    );
  }

  if (!user) {
    return (
      <main className="wishlist-state">
        <Heart size={42} strokeWidth={1.3} />
        <h1>Your Wishlist</h1>
        <p>Sign in to view your saved pieces.</p>
        <Link className="wishlist-primary-button" to="/login">
          Sign In
        </Link>
      </main>
    );
  }

  return (
    <main className="wishlist-page">
      <header className="wishlist-heading">
        <p className="wishlist-eyebrow">YOUR PERSONAL COLLECTION</p>
        <h1>Wishlist</h1>
        <p>
          {items.length} {items.length === 1 ? "piece" : "pieces"} saved
        </p>
      </header>

      {error && (
        <div className="wishlist-error" role="alert">
          {error}
          <button type="button" onClick={fetchWishlist}>
            Retry
          </button>
        </div>
      )}

      {items.length === 0 ? (
        <section className="wishlist-empty">
          <Heart size={44} strokeWidth={1.2} />
          <h2>Nothing saved. Yet.</h2>
          <p>
            Keep the pieces you love close by saving them to your wishlist.
          </p>
          <Link to="/products" className="wishlist-primary-button">
            <ShoppingBag size={17} />
            Explore Collection
          </Link>
        </section>
      ) : (
        <section className="wishlist-grid">
          {items.map((item) => {
            const price = item.discountPrice ?? item.price;
            const hasDiscount =
              item.discountPrice != null &&
              Number(item.discountPrice) < Number(item.price);

            return (
              <article
                className="wishlist-card"
                key={item.wishlistItemId}
              >
                <Link
                  to={`/products/${item.productId}`}
                  className="wishlist-card-image"
                  aria-label={`View ${item.productName}`}
                >
                  <div className="wishlist-image-placeholder">
                    <ShoppingBag size={36} strokeWidth={1.1} />
                  </div>
                  <span className="wishlist-saved-label">SAVED</span>
                </Link>

                <div className="wishlist-card-info">
                  <Link
                    to={`/products/${item.productId}`}
                    className="wishlist-product-name"
                  >
                    {item.productName}
                  </Link>

                  <div className="wishlist-prices">
                    <span>{formatPrice(price)}</span>

                    {hasDiscount && (
                      <span className="wishlist-original-price">
                        {formatPrice(item.price)}
                      </span>
                    )}
                  </div>

                  <div className="wishlist-card-actions">
                    <Link
                      to={`/products/${item.productId}`}
                      className="wishlist-view-button"
                    >
                      View Product
                    </Link>

                    <button
                      type="button"
                      className="wishlist-remove-button"
                      aria-label={`Remove ${item.productName} from wishlist`}
                      disabled={removingId !== null}
                      onClick={() => removeItem(item)}
                    >
                      <Trash2 size={17} />
                    </button>
                  </div>
                </div>
              </article>
            );
          })}
        </section>
      )}
    </main>
  );
}
