
import { useState } from "react";
import { Heart } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";

export default function WishlistButton({
  productId,
  initialSaved = false,
  onChange,
}) {
  const { user } = useAuth();
  const [saved, setSaved] = useState(initialSaved);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function toggleWishlist(event) {
    event.preventDefault();
    event.stopPropagation();

    if (!user) {
      window.location.href = "/login";
      return;
    }

    if (loading) return;

    setLoading(true);
    setError("");

    try {
      if (saved) {
        const response = await api.get("/wishlist");
        const item = response.data.find(
          (entry) => entry.productId === productId
        );

        if (item) {
          await api.delete(`/wishlist/items/${item.wishlistItemId}`);
        }

        setSaved(false);
        onChange?.(false);
      } else {
        await api.post("/wishlist/items", null, {
          params: { productId },
        });

        setSaved(true);
        onChange?.(true);
      }
    } catch (err) {
      setError(
        err.response?.data?.message || "Could not update wishlist."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="wishlist-button-wrap">
      <button
        type="button"
        className={`wishlist-heart-button ${saved ? "is-saved" : ""}`}
        onClick={toggleWishlist}
        disabled={loading}
        aria-label={saved ? "Remove from wishlist" : "Add to wishlist"}
        aria-pressed={saved}
        title={error || (saved ? "Remove from wishlist" : "Add to wishlist")}
      >
        <Heart
          size={19}
          fill={saved ? "currentColor" : "none"}
          strokeWidth={1.7}
        />
      </button>
      {error && <span className="wishlist-button-error">{error}</span>}
    </div>
  );
}
