
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  ArrowDownRight,
  ArrowRight,
  ArrowUpRight,
  Check,
  Leaf,
  ShieldCheck,
  Star,
  Truck,
} from "lucide-react";
import api from "../api/api";

const fallbackProducts = [
  {
    id: "sample-1",
    name: "Relaxed Essential Tee",
    category: "EVERYDAY ESSENTIALS",
    price: 899,
    image:
      "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: "sample-2",
    name: "Minimal Oversized Shirt",
    category: "MODERN CLASSICS",
    price: 1499,
    image:
      "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: "sample-3",
    name: "Everyday Streetwear",
    category: "NEW SEASON",
    price: 1799,
    image:
      "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: "sample-4",
    name: "Urban Relaxed Fit",
    category: "BESTSELLERS",
    price: 1299,
    image:
      "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=900&q=85",
  },
];

const formatPrice = (price) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(Number(price) || 0);

function getProductImage(product) {
  return (
    product.imageUrl ||
    product.image ||
    product.thumbnail ||
    product.productImage ||
    product.images?.[0]?.imageUrl ||
    product.images?.[0] ||
    "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=85"
  );
}

function ProductCard({ product, index }) {
  const id = product.id ?? product.productId;
  const name = product.name ?? product.title ?? "Wearora Essential";
  const category =
    product.category?.name ??
    product.categoryName ??
    product.category ??
    "NEW SEASON";

  return (
    <article className="fashion-card" key={id ?? name}>
      <Link
        to={id && !String(id).startsWith("sample-") ? `/products/${id}` : "/products"}
        className="fashion-card-image"
        aria-label={`View ${name}`}
      >
        <img
          src={getProductImage(product)}
          alt={name}
          loading={index > 2 ? "lazy" : "eager"}
        />
        <span className={`product-badge ${index === 0 ? "badge-dark" : ""}`}>
          {product.badge || category}
        </span>
        <span className="card-arrow" aria-hidden="true">
          <ArrowUpRight size={19} />
        </span>
      </Link>

      <div className="fashion-card-info">
        <div>
          <p className="eyebrow">{category}</p>
          <h3>{name}</h3>
        </div>
        <p className="product-price">
          {formatPrice(product.price ?? product.sellingPrice)}
        </p>
      </div>
    </article>
  );
}

export default function Home() {
  const [products, setProducts] = useState(fallbackProducts);

  useEffect(() => {
    let active = true;

    async function loadProducts() {
      try {
        const response = await api.get("/products", {
          params: { page: 0, size: 6, sort: "createdAt,desc" },
        });

        const payload = response.data;
        const items = Array.isArray(payload)
          ? payload
          : payload?.content ??
            payload?.products ??
            payload?.data ??
            payload?.items ??
            [];

        if (active && items.length > 0) {
          setProducts(items);
        }
      } catch (error) {
        console.error(
          "Could not load products from the Wearora API. Showing demo products.",
          error
        );
      }
    }

    loadProducts();

    return () => {
      active = false;
    };
  }, []);

  const arrivals = products.slice(0, 3);
  const collection = products.slice(0, 6);

  return (
    <main className="wearora-home">
      <section className="hero-shell">
        <div className="hero-banner">
          <img
            src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=2000&q=90"
            alt="Wearora contemporary fashion collection"
          />

          <div className="hero-overlay" />

          <div className="hero-topline">
            <span className="eyebrow">THE NEW EVERYDAY</span>
            <span className="hero-brand">wearora.</span>
            <span className="eyebrow">EST. FOR YOUR STYLE</span>
          </div>

          <div className="hero-copy">
            <p className="eyebrow">LESS, BUT BETTER.</p>
            <h1>
              Style that
              <br />
              speaks <em>quietly.</em>
            </h1>
            <p>
              Thoughtful essentials. Effortless silhouettes.
              Made for the way you move.
            </p>
            <Link to="/products" className="pill-button">
              Explore the collection <ArrowUpRight size={18} />
            </Link>
          </div>

          <div className="hero-bottomline">
            <span>01 / NEW COLLECTION</span>
            <span>DESIGNED FOR EVERY DAY</span>
            <ArrowDownRight size={20} />
          </div>
        </div>
      </section>

      <section className="home-section arrivals-section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">FRESHLY CURATED</p>
            <h2>New arrivals<span>.</span></h2>
          </div>
          <Link to="/products" className="round-link" aria-label="View all products">
            <ArrowUpRight size={22} />
          </Link>
        </div>

        <div className="fashion-grid three-grid">
          {arrivals.map((product, index) => (
            <ProductCard
              key={product.id ?? product.productId ?? product.name ?? index}
              product={product}
              index={index}
            />
          ))}
        </div>

        <div className="section-footer">
          <Link to="/products" className="text-link">
            Discover all pieces <ArrowRight size={16} />
          </Link>
        </div>
      </section>

      <section className="wardrobe-section">
        <div className="wardrobe-copy">
          <p className="eyebrow">THE WEARORA APPROACH</p>
          <h2>
            A better
            <br />
            kind of <em>basic.</em>
          </h2>
          <p>
            Fewer pieces. More possibilities. Discover everyday clothing
            designed to work together and feel like you.
          </p>
          <Link to="/products" className="dark-button pill-button">
            Find your essentials <ArrowUpRight size={18} />
          </Link>
        </div>

        <div className="wardrobe-image">
          <img
            src="https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=85"
            alt="A curated contemporary wardrobe"
            loading="lazy"
          />
          <span className="wardrobe-image-label">THE EVERYDAY EDIT — 2026</span>
        </div>
      </section>

      <section className="home-section why-section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">WHY WEARORA</p>
            <h2>Considered in every detail<span>.</span></h2>
          </div>
        </div>

        <div className="benefit-grid">
          <article className="benefit-card benefit-cream">
            <span className="benefit-symbol"><Leaf size={25} /></span>
            <p className="eyebrow">01 — THOUGHTFUL DESIGN</p>
            <h3>Less noise.<br />More intention.</h3>
            <p>Versatile styles that earn their place in your wardrobe.</p>
          </article>

          <article className="benefit-card benefit-photo">
            <img
              src="https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=900&q=85"
              alt="Modern everyday fashion"
              loading="lazy"
            />
            <div className="benefit-photo-copy">
              <p className="eyebrow">02 — PERSONAL STYLE</p>
              <h3>Be yourself.<br />Every day.</h3>
            </div>
          </article>

          <article className="benefit-card benefit-black">
            <span className="benefit-symbol"><ShieldCheck size={25} /></span>
            <p className="eyebrow">03 — SHOP WITH CONFIDENCE</p>
            <h3>Easy choices.<br />Easy shopping.</h3>
            <p>Discover your next favourite piece without the fuss.</p>
          </article>
        </div>

        <div className="benefit-trust-row">
          <span><Check size={16} /> Thoughtful essentials</span>
          <span><Truck size={17} /> Convenient ordering</span>
          <span><ShieldCheck size={17} /> Secure checkout</span>
        </div>
      </section>

      <section className="home-section collection-section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">MADE TO BE WORN</p>
            <h2>Pieces to live in<span>.</span></h2>
          </div>
          <Link to="/products" className="text-link">
            Shop all <ArrowRight size={16} />
          </Link>
        </div>

        <div className="fashion-grid">
          {collection.map((product, index) => (
            <ProductCard
              key={product.id ?? product.productId ?? product.name ?? index}
              product={product}
              index={index}
            />
          ))}
        </div>
      </section>

      <section className="home-section streets-section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">OUT IN THE WORLD</p>
            <h2>Your style, your story<span>.</span></h2>
          </div>
        </div>

        <div className="editorial-grid">
          <Link to="/products" className="editorial-tile editorial-person">
            <img
              src="https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=1100&q=85"
              alt="Wearora street style inspiration"
              loading="lazy"
            />
            <span className="editorial-caption">THE STREET EDIT <ArrowUpRight size={17} /></span>
          </Link>

          <Link to="/products" className="editorial-tile editorial-watch">
            <img
              src="https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=85"
              alt="Minimal accessories inspiration"
              loading="lazy"
            />
            <span className="editorial-caption">THE FINISHING TOUCH</span>
          </Link>

          <div className="editorial-tile editorial-cap">
            <img
              src="https://images.unsplash.com/photo-1521369909029-2afed882baee?auto=format&fit=crop&w=800&q=85"
              alt="Everyday casual style inspiration"
              loading="lazy"
            />
            <div className="promo-label">
              <p className="eyebrow">YOUR NEXT FAVOURITE</p>
              <h3>Make room<br />for better.</h3>
              <Link to="/products" className="mini-cta">
                Shop the edit <ArrowUpRight size={16} />
              </Link>
            </div>
          </div>
        </div>
      </section>

      <section className="testimonial-section">
        <p className="eyebrow">A LITTLE LOVE FOR THE EVERYDAY</p>
        <div className="testimonial-quote">
          <div className="stars" aria-label="5 out of 5 stars">
            {Array.from({ length: 5 }, (_, index) => (
              <Star key={index} size={17} fill="currentColor" />
            ))}
          </div>
          <blockquote>
            “The kind of pieces you reach for again and again.
            Simple, comfortable, and completely my style.”
          </blockquote>
          <p className="eyebrow">THE WEARORA WAY</p>
        </div>
      </section>

      <section className="final-cta">
        <img
          src="https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1800&q=90"
          alt="Discover the Wearora collection"
          loading="lazy"
        />
        <div className="final-cta-copy">
          <p className="eyebrow">YOUR WARDROBE, REIMAGINED</p>
          <h2>Good style.<br /><em>No overthinking.</em></h2>
          <p>Find the pieces that feel right, every single day.</p>
          <Link to="/products" className="pill-button">
            Shop Wearora <ArrowUpRight size={18} />
          </Link>
        </div>
      </section>
    </main>
  );
}
