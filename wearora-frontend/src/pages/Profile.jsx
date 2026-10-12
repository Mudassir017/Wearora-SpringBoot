import { Link, useNavigate } from "react-router-dom";
import { UserRound, Package, Heart, ShoppingBag, LogOut, MapPin } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import "./Profile.css";

export default function Profile() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  if (!user) {
    return (
      <main className="profile-page">
        <h1>Your account</h1>
        <p>Please log in to view your account.</p>
        <Link to="/login" className="profile-primary-button">
          Login to continue
        </Link>
      </main>
    );
  }

  return (
    <main className="profile-page">
      <header className="profile-heading">
        <p className="profile-eyebrow">WEARORA / MY ACCOUNT</p>
        <h1>Your space.</h1>
        <p>Manage your account and keep track of your orders.</p>
      </header>

      <section className="profile-card">
        <div className="profile-avatar">
          <UserRound size={34} strokeWidth={1.4} />
        </div>

        <div className="profile-user-info">
          <span className="profile-label">SIGNED IN AS</span>
          <h2>{user.name || "Wearora Customer"}</h2>
          <p>{user.email || "Email unavailable"}</p>
        </div>

        <span className="profile-member-tag">CUSTOMER</span>
      </section>

      <section className="profile-menu">
        <Link to="/orders" className="profile-menu-item">
          <span className="profile-menu-icon"><Package size={21} /></span>
          <span className="profile-menu-copy">
            <strong>My orders</strong>
            <small>View purchases and track order status</small>
          </span>
          <span className="profile-menu-arrow">→</span>
        </Link>

        <Link to="/wishlist" className="profile-menu-item">
          <span className="profile-menu-icon"><Heart size={21} /></span>
          <span className="profile-menu-copy">
            <strong>My wishlist</strong>
            <small>Find the pieces you've saved</small>
          </span>
          <span className="profile-menu-arrow">→</span>
        </Link>
		
		<Link to="/addresses" className="profile-menu-item">
		  <span className="profile-menu-icon">
		    <MapPin size={21} />
		  </span>

		  <span className="profile-menu-copy">
		    <strong>My Addresses</strong>
		    <small>Manage your saved delivery addresses</small>
		  </span>

		  <span className="profile-menu-arrow">→</span>
		</Link>

        <Link to="/cart" className="profile-menu-item">
          <span className="profile-menu-icon"><ShoppingBag size={21} /></span>
          <span className="profile-menu-copy">
            <strong>My bag</strong>
            <small>Review items before checkout</small>
          </span>
          <span className="profile-menu-arrow">→</span>
        </Link>
      </section>

      <button className="profile-logout-button" onClick={handleLogout}>
        <LogOut size={17} />
        Log out
      </button>
    </main>
  );
}