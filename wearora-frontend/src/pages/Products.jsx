import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ShoppingBag } from "lucide-react";
import api from "../api/api";
import WishlistButton from "../components/WishlistButton";

function formatPrice(value) {
  return `₹${Number(value ?? 0).toLocaleString("en-IN")}`;
}

function getProductsFromResponse(data) {
  if (typeof data === "string") {
    try {
      data = JSON.parse(data);
    } catch {
      return [];
    }
  }

  // API response may be a direct array.
  if (Array.isArray(data)) {
    return data;
  }

  // API response may contain products in a nested property.
  if (Array.isArray(data?.products)) {
    return data.products;
  }

  if (Array.isArray(data?.content)) {
    return data.content;
  }

  if (Array.isArray(data?.items)) {
    return data.items;
  }

  if (Array.isArray(data?.data)) {
    return data.data;
  }

  if (Array.isArray(data?.results)) {
    return data.results;
  }

  console.error("Unrecognized products API response:", data);
  return [];
}

function getTotalPages(data, products) {
  if (Number.isFinite(data?.totalPages)) {
    return data.totalPages;
  }

  if (Number.isFinite(data?.page?.totalPages)) {
    return data.page.totalPages;
  }

  if (Number.isFinite(data?.totalElements)) {
    return Math.max(1, Math.ceil(data.totalElements / 12));
  }

  return products.length > 0 ? 1 : 0;
}

export default function Product() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [sort, setSort] = useState("price,asc");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const params = {
        page,
        size: 12,
        sort,
      };

      if (search.trim()) params.search = search.trim();
      if (category.trim()) params.category = category.trim();
      if (minPrice !== "") params.minPrice = minPrice;
      if (maxPrice !== "") params.maxPrice = maxPrice;

      const response = await api.get("/products", { params });
      const data = response.data;

      // Debugging: inspect actual API response in browser Console.
      console.log("Products API response:", data);

      const extractedProducts = getProductsFromResponse(data);

      console.log("Products extracted:", extractedProducts);

      setProducts(extractedProducts);
      setTotalPages(getTotalPages(data, extractedProducts));
    } catch (err) {
      console.error("Failed to fetch products:", err);

      setProducts([]);
      setTotalPages(0);

      setError(
        err.response?.data?.message ??
          "Unable to load products. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }, [page, search, category, minPrice, maxPrice, sort]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  function handleFilterSubmit(event) {
    event.preventDefault();

    if (page !== 0) {
      setPage(0);
      return;
    }

    fetchProducts();
  }

  function clearFilters() {
    setSearch("");
    setCategory("");
    setMinPrice("");
    setMaxPrice("");
    setSort("price,asc");
    setPage(0);
  }

  return (
    <main className="products-page">
      <header className="products-heading">
        <p className="products-eyebrow">THE WEARORA COLLECTION</p>
        <h1>Discover Your Style</h1>
        <p>Considered pieces for your everyday wardrobe.</p>
      </header>

      <form className="products-filters" onSubmit={handleFilterSubmit}>
        <input
          type="search"
          placeholder="Search products..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          aria-label="Search products"
        />

        <input
          type="text"
          placeholder="Category"
          value={category}
          onChange={(event) => setCategory(event.target.value)}
          aria-label="Category"
        />

        <input
          type="number"
          min="0"
          placeholder="Min price"
          value={minPrice}
          onChange={(event) => setMinPrice(event.target.value)}
          aria-label="Minimum price"
        />

        <input
          type="number"
          min="0"
          placeholder="Max price"
          value={maxPrice}
          onChange={(event) => setMaxPrice(event.target.value)}
          aria-label="Maximum price"
        />

        <select
          value={sort}
          onChange={(event) => {
            setSort(event.target.value);
            setPage(0);
          }}
          aria-label="Sort products"
        >
          <option value="price,asc">Price: Low to High</option>
          <option value="price,desc">Price: High to Low</option>
          <option value="productName,asc">Name: A to Z</option>
          <option value="productName,desc">Name: Z to A</option>
          <option value="createdAt,desc">Newest First</option>
        </select>

        <button type="submit">Apply Filters</button>

        <button
          type="button"
          className="products-clear"
          onClick={clearFilters}
        >
          Clear
        </button>
      </form>

      {error && (
        <div className="products-error" role="alert">
          {error}
          <button type="button" onClick={fetchProducts}>
            Retry
          </button>
        </div>
      )}

      {loading ? (
        <div className="products-state">Loading collection...</div>
      ) : products.length === 0 ? (
        <div className="products-state">
          <ShoppingBag size={36} strokeWidth={1.3} />
          <h2>No products found</h2>
          <p>Try changing your search or filters.</p>

          <button type="button" onClick={clearFilters}>
            Clear filters
          </button>
        </div>
      ) : (
        <>
          <section className="products-grid">
            {products.map((product) => {
              const images = Array.isArray(product.images)
                ? product.images
                : [];

              const mainImage =
                images.find((image) => image.primary)?.imageUrl ??
                images[0]?.imageUrl ??
                "";

              const price = product.discountPrice ?? product.price;

              const hasDiscount =
                product.discountPrice != null &&
                Number(product.discountPrice) < Number(product.price);

              const productName =
                product.productName ?? product.name ?? "Untitled Product";

              const productId = product.id ?? product.productId;

              return (
                <article className="product-card" key={productId}>
                  <div className="product-card-visual">
                    <Link
                      to={`/products/${productId}`}
                      className="product-card-image"
                    >
                      {mainImage ? (
                        <img
                          src={mainImage}
                          alt={productName}
                          loading="lazy"
                        />
                      ) : (
                        <div className="product-card-placeholder">
                          <ShoppingBag size={36} strokeWidth={1.2} />
                        </div>
                      )}
                    </Link>

                    <div className="product-card-wishlist">
                      <WishlistButton productId={productId} />
                    </div>

                    {hasDiscount && (
                      <span className="product-card-badge">SALE</span>
                    )}
                  </div>

                  <div className="product-card-info">
                    <p className="product-card-category">
                      {product.categoryName ??
                        product.category?.categoryName ??
                        ""}
                    </p>

                    <Link
                      to={`/products/${productId}`}
                      className="product-card-name"
                    >
                      {productName}
                    </Link>

                    <div className="product-card-prices">
                      <span>{formatPrice(price)}</span>

                      {hasDiscount && (
                        <span className="product-card-original-price">
                          {formatPrice(product.price)}
                        </span>
                      )}
                    </div>

                    <Link
                      to={`/products/${productId}`}
                      className="product-card-view"
                    >
                      View Product
                    </Link>
                  </div>
                </article>
              );
            })}
          </section>

          {totalPages > 1 && (
            <nav
              className="products-pagination"
              aria-label="Product pages"
            >
              <button
                type="button"
                disabled={page === 0}
                onClick={() => setPage((current) => current - 1)}
              >
                Previous
              </button>

              <span>
                Page {page + 1} of {totalPages}
              </span>

              <button
                type="button"
                disabled={page + 1 >= totalPages}
                onClick={() => setPage((current) => current + 1)}
              >
                Next
              </button>
            </nav>
          )}
        </>
      )}
    </main>
  );
}