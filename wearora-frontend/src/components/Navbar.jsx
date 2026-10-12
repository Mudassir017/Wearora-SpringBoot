import { Link, NavLink } from "react-router-dom";
import {
  Heart,
  ShoppingBag,
  UserRound,
  Package,
  LogOut,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="navbar">
      <Link to="/" className="brand">
        wearora<span>.</span>
      </Link>

      <nav className="nav-links">
        <NavLink to="/">Home</NavLink>
        <NavLink to="/products">Shop</NavLink>

        <NavLink to="/wishlist" aria-label="Wishlist" title="Wishlist">
          <Heart size={18} />
        </NavLink>

        <NavLink to="/cart" aria-label="Shopping Bag" title="Shopping Bag">
          <ShoppingBag size={18} />
        </NavLink>
      </nav>

      <div className="nav-account">
        {user ? (
          <>
            <Link to="/profile" className="account-link">
              <UserRound size={17} />
              <span>Profile</span>
            </Link>

            <Link to="/orders" className="account-link">
              <Package size={17} />
              <span>My Orders</span>
            </Link>

            <button
              type="button"
              className="text-button"
              onClick={logout}
            >
              <LogOut size={16} />
              <span>Logout</span>
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="account-link">
              <UserRound size={17} />
              <span>Login</span>
            </Link>

            <Link to="/register" className="account-link">
              Register
            </Link>
          </>
        )}
      </div>
    </header>
  );
}