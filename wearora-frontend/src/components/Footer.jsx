import { Link } from "react-router-dom";

export default function Footer() {
  return (
    <footer className="footer">
      <div>
        <Link to="/" className="brand">wearora<span>.</span></Link>
        <p>Everyday essentials. Thoughtfully selected.</p>
      </div>

      <div className="footer-links">
        <Link to="/products">Shop</Link>
        <Link to="/orders">Your orders</Link>
        <Link to="/wishlist">Wishlist</Link>
      </div>

      <p className="copyright">
        © {new Date().getFullYear()} Wearora
      </p>
    </footer>
  );
}