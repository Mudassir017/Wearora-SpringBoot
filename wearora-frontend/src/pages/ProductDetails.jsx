import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, ShoppingBag } from "lucide-react";

import api from "../api/api";
import { useAuth } from "../context/AuthContext";
import WishlistButton from "../components/WishlistButton";

import "./ProductDetails.css";

function formatPrice(value) {
  return `₹${Number(value ?? 0).toLocaleString("en-IN")}`;
}

export default function ProductDetails() {
  const { id } = useParams();
  const { user } = useAuth();

  const [product, setProduct] = useState(null);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [activeImage, setActiveImage] = useState("");
  const [quantity, setQuantity] = useState(1);

  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function fetchProduct() {
      setLoading(true);
      setError("");
      setMessage("");
      setProduct(null);
      setSelectedVariant(null);
      setActiveImage("");
      setQuantity(1);

      try {
        const response = await api.get(`/products/${id}`);

        if (cancelled) return;

        const data = response.data;
        setProduct(data);

        const images = [...(data.images ?? [])].sort((a, b) => {
          if (a.primary !== b.primary) {
            return a.primary ? -1 : 1;
          }

          return (a.displayOrder ?? 0) - (b.displayOrder ?? 0);
        });

        setActiveImage(images[0]?.imageUrl ?? "");

        const availableVariants = (data.variants ?? []).filter(
          (variant) => Number(variant.stockQuantity) > 0
        );

        // Automatically select One Size for accessories.
        const oneSizeVariant = availableVariants.find(
          (variant) =>
            variant.size?.trim().toLowerCase() === "one size"
        );

        // Otherwise, automatically select the first available variant.
        const defaultVariant =
          oneSizeVariant ?? availableVariants[0] ?? null;

        setSelectedVariant(defaultVariant);
      } catch (err) {
        if (!cancelled) {
          setError(
            err.response?.data?.message ??
              "Unable to load this product. Please try again."
          );
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    fetchProduct();

    return () => {
      cancelled = true;
    };
  }, [id]);

  const variants = product?.variants ?? [];

  // Only show the size selector for products with actual size options.
  const hasSizeOptions = variants.some((variant) => {
    const size = variant.size?.trim().toLowerCase();

    return size && size !== "one size";
  });

  const stock = Number(selectedVariant?.stockQuantity ?? 0);

  const canAddToCart =
    selectedVariant != null &&
    stock > 0 &&
    quantity >= 1 &&
    quantity <= stock;

  const images = [...(product?.images ?? [])].sort((a, b) => {
    if (a.primary !== b.primary) {
      return a.primary ? -1 : 1;
    }

    return (a.displayOrder ?? 0) - (b.displayOrder ?? 0);
  });

  const price = product
    ? product.discountPrice ?? product.price
    : 0;

  const hasDiscount =
    product?.discountPrice != null &&
    Number(product.discountPrice) < Number(product.price);

  async function handleAddToCart() {
    setError("");
    setMessage("");

    if (!user) {
      setError("Please sign in before adding items to your cart.");
      return;
    }

    if (!selectedVariant) {
      setError(
        hasSizeOptions
          ? "Please select an available size."
          : "This product has no available stock."
      );
      return;
    }

    if (Number(selectedVariant.stockQuantity) < 1) {
      setError("This product is currently out of stock.");
      return;
    }

    if (quantity > Number(selectedVariant.stockQuantity)) {
      setError("Quantity exceeds available stock.");
      return;
    }

    setAdding(true);

    try {
      await api.post("/cart/items", null, {
        params: {
          variantId: selectedVariant.id,
          quantity,
        },
      });

      setMessage("Added to your shopping bag.");
    } catch (err) {
      setError(
        err.response?.data?.message ??
          err.response?.data?.error ??
          "Unable to add this product to your cart."
      );
    } finally {
      setAdding(false);
    }
  }

  if (loading) {
    return (
      <main className="product-details-state">
        Loading product...
      </main>
    );
  }

  if (!product) {
    return (
      <main className="product-details-state">
        <p>{error || "Product not found."}</p>
        <Link to="/products">Back to shop</Link>
      </main>
    );
  }

  return (
    <main className="product-details-page">
      <Link to="/products" className="product-back-link">
        <ArrowLeft size={17} />
        Back to shop
      </Link>

      <section className="product-details-layout">
        <div className="product-gallery">
          <div className="product-main-image">
            {activeImage ? (
              <img
                src={activeImage}
                alt={product.productName}
              />
            ) : (
              <div className="product-image-placeholder">
                <ShoppingBag size={42} strokeWidth={1.2} />
                <span>No image available</span>
              </div>
            )}

            <div className="product-details-wishlist">
              <WishlistButton productId={product.id} />
            </div>
          </div>

          {images.length > 1 && (
            <div className="product-thumbnail-list">
              {images.map((image) => (
                <button
                  type="button"
                  key={image.id}
                  className={
                    activeImage === image.imageUrl
                      ? "product-thumbnail active"
                      : "product-thumbnail"
                  }
                  onClick={() => setActiveImage(image.imageUrl)}
                  aria-label="View product image"
                >
                  <img
                    src={image.imageUrl}
                    alt={product.productName}
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="product-details-info">
          <p className="product-detail-category">
            {product.categoryName}
          </p>

          <h1>{product.productName}</h1>

          {product.brand && (
            <p className="product-detail-brand">
              {product.brand}
            </p>
          )}

          <div className="product-detail-prices">
            <span className="product-detail-price">
              {formatPrice(price)}
            </span>

            {hasDiscount && (
              <span className="product-detail-original-price">
                {formatPrice(product.price)}
              </span>
            )}
          </div>

          {product.description && (
            <p className="product-detail-description">
              {product.description}
            </p>
          )}

          {product.material && (
            <p className="product-detail-material">
              <strong>Material:</strong> {product.material}
            </p>
          )}

          {/* Show sizes only when the product has actual size options. */}
          {hasSizeOptions && (
            <div className="product-size-section">
              <h3>Select size</h3>

              <div className="product-size-options">
                {variants
                  .filter((variant) => {
                    const size = variant.size?.trim().toLowerCase();
                    return size && size !== "one size";
                  })
                  .map((variant) => {
                    const available =
                      Number(variant.stockQuantity) > 0;

                    return (
                      <button
                        type="button"
                        key={variant.id}
                        disabled={!available}
                        className={
                          selectedVariant?.id === variant.id
                            ? "product-size-button selected"
                            : "product-size-button"
                        }
                        onClick={() => {
                          setSelectedVariant(variant);
                          setQuantity(1);
                          setError("");
                          setMessage("");
                        }}
                      >
                        {variant.size}
                      </button>
                    );
                  })}
              </div>

              <p
                className={
                  stock > 0
                    ? "product-stock-status"
                    : "product-stock-unavailable"
                }
              >
                {stock > 0
                  ? `${stock} available`
                  : "Select an available size"}
              </p>
            </div>
          )}

          {/* Accessories can use their automatically selected One Size variant. */}
          {!hasSizeOptions && (
            <p
              className={
                stock > 0
                  ? "product-stock-status"
                  : "product-stock-unavailable"
              }
            >
              {stock > 0
                ? `${stock} available`
                : "Currently out of stock"}
            </p>
          )}

          <div className="product-quantity-section">
            <label htmlFor="product-quantity">
              Quantity
            </label>

            <input
              id="product-quantity"
              type="number"
              min="1"
              max={stock}
              value={quantity}
              disabled={stock < 1}
              onChange={(event) => {
                const value = Number(event.target.value);

                if (Number.isInteger(value) && value >= 1) {
                  setQuantity(Math.min(value, stock));
                }
              }}
            />
          </div>

          <button
            type="button"
            className="product-add-cart-button"
            onClick={handleAddToCart}
            disabled={adding || !canAddToCart}
          >
            <ShoppingBag size={18} />
            {adding ? "Adding..." : "Add to Cart"}
          </button>

          {message && (
            <p className="product-feedback-success">
              {message}
            </p>
          )}

          {error && (
            <p className="product-feedback-error">
              {error}
            </p>
          )}
        </div>
      </section>
    </main>
  );
}