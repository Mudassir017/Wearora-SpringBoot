import { useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Package, ArrowLeft, ArrowRight, XCircle } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import api from "../api/api";
import "./MyOrders.css";

export default function MyOrders() {
  const { orderId } = useParams();
  const { user } = useAuth();

  const [orders, setOrders] = useState([]);
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [cancelling, setCancelling] = useState(false);

  const money = (value) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 2,
    }).format(Number(value || 0));

  const formatDate = (value) => {
    if (!value) return "Date unavailable";

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return "Date unavailable";

    return date.toLocaleDateString("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  };

  const loadOrders = useCallback(async () => {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      if (orderId) {
        const response = await api.get(`/orders/${orderId}`);
        setOrder(response.data);
      } else {
        const response = await api.get("/orders");
        setOrders(Array.isArray(response.data) ? response.data : []);
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to load your orders. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }, [orderId]);

  useEffect(() => {
    if (user) loadOrders();
  }, [user, loadOrders]);

  async function handleCancel(id) {
    const confirmed = window.confirm(
      `Are you sure you want to cancel order #${id}?`
    );

    if (!confirmed) return;

    setCancelling(true);
    setError("");
    setMessage("");

    try {
      const response = await api.patch(`/orders/${id}/cancel`);

      if (orderId) {
        setOrder(response.data);
      } else {
        setOrders((previous) =>
          previous.map((item) =>
            item.orderId === id ? response.data : item
          )
        );
      }

      setMessage(`Order #${id} has been cancelled successfully.`);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to cancel this order."
      );
    } finally {
      setCancelling(false);
    }
  }

  if (!user) {
    return (
      <main className="my-orders-page">
        <h1>Your orders</h1>
        <p>Please log in to view your orders.</p>
        <Link to="/login" className="orders-primary-button">
          Login to continue
        </Link>
      </main>
    );
  }

  if (loading) {
    return (
      <main className="my-orders-page">
        <p>Loading your orders...</p>
      </main>
    );
  }

  if (error && !order && orderId) {
    return (
      <main className="my-orders-page">
        <h1>Order unavailable</h1>
        <p className="orders-error">{error}</p>
        <Link to="/orders" className="orders-back-link">
          <ArrowLeft size={16} /> Back to my orders
        </Link>
      </main>
    );
  }

  if (orderId && order) {
    return (
      <main className="my-orders-page">
        <Link to="/orders" className="orders-back-link">
          <ArrowLeft size={16} /> Back to my orders
        </Link>

        <header className="orders-heading">
          <p className="orders-eyebrow">WEARORA / ORDER DETAILS</p>
          <h1>Order #{order.orderId}</h1>
          <p>Placed on {formatDate(order.orderDate)}</p>
        </header>

        {error && <p className="orders-error">{error}</p>}
        {message && <p className="orders-success">{message}</p>}

        <section className="order-detail-card">
          <div className="order-detail-top">
            <div>
              <span className="orders-muted">Order status</span>
              <StatusBadge status={order.status} />
            </div>
            <div>
              <span className="orders-muted">Order total</span>
              <strong>{money(order.totalAmount)}</strong>
            </div>
          </div>

          <h2>Items ordered</h2>

          {order.items?.map((item) => (
            <div
              className="orders-item"
              key={item.orderItemId ?? item.id ?? item.variantId}
            >
              <div>
                <strong>{item.productName}</strong>
                <span>Size: {item.size}</span>
                <span>Quantity: {item.quantity}</span>
              </div>
              <strong>
                {money(Number(item.unitPrice) * Number(item.quantity))}
              </strong>
            </div>
          ))}

          <div className="orders-total-row">
            <span>Total</span>
            <strong>{money(order.totalAmount)}</strong>
          </div>

          <h2>Delivery details</h2>
          <div className="orders-delivery-details">
            <strong>{order.deliveryName}</strong>
            <span>{order.deliveryPhone}</span>
            <span>{order.shippingAddress}</span>
          </div>

          <div className="orders-payment-details">
            <div>
              <span>Payment method</span>
              <strong>{order.paymentMethod || "COD"}</strong>
            </div>
            <div>
              <span>Payment status</span>
              <strong>{order.paymentStatus || "PENDING"}</strong>
            </div>
          </div>

          {canCancel(order.status) && (
            <button
              className="orders-cancel-button"
              onClick={() => handleCancel(order.orderId)}
              disabled={cancelling}
            >
              <XCircle size={17} />
              {cancelling ? "Cancelling..." : "Cancel order"}
            </button>
          )}
        </section>
      </main>
    );
  }

  return (
    <main className="my-orders-page">
      <header className="orders-heading">
        <p className="orders-eyebrow">WEARORA / YOUR ACCOUNT</p>
        <h1>My orders.</h1>
        <p>Everything you've ordered, all in one place.</p>
      </header>

      {error && <p className="orders-error">{error}</p>}
      {message && <p className="orders-success">{message}</p>}

      {orders.length === 0 ? (
        <section className="orders-empty">
          <Package size={42} strokeWidth={1.3} />
          <h2>No orders yet</h2>
          <p>Your next favourite piece is waiting for you.</p>
          <Link to="/products" className="orders-primary-button">
            Explore the collection <ArrowRight size={17} />
          </Link>
        </section>
      ) : (
        <div className="orders-list">
          {orders.map((item) => (
            <article className="orders-card" key={item.orderId}>
              <div className="orders-card-header">
                <div>
                  <span className="orders-muted">Order number</span>
                  <strong>#{item.orderId}</strong>
                </div>
                <StatusBadge status={item.status} />
              </div>

              <div className="orders-card-meta">
                <div>
                  <span className="orders-muted">Order date</span>
                  <strong>{formatDate(item.orderDate)}</strong>
                </div>
                <div>
                  <span className="orders-muted">Items</span>
                  <strong>{item.items?.length || 0}</strong>
                </div>
                <div>
                  <span className="orders-muted">Total</span>
                  <strong>{money(item.totalAmount)}</strong>
                </div>
              </div>

              <div className="orders-card-footer">
                <Link
                  to={`/orders/${item.orderId}`}
                  className="orders-details-link"
                >
                  View details <ArrowRight size={16} />
                </Link>

                {canCancel(item.status) && (
                  <button
                    className="orders-cancel-button"
                    onClick={() => handleCancel(item.orderId)}
                    disabled={cancelling}
                  >
                    {cancelling ? "Please wait..." : "Cancel order"}
                  </button>
                )}
              </div>
            </article>
          ))}
        </div>
      )}
    </main>
  );
}

function canCancel(status) {
  return ["PLACED", "CONFIRMED", "PROCESSING"].includes(
    String(status || "").toUpperCase()
  );
}

function StatusBadge({ status }) {
  const normalized = String(status || "UNKNOWN").toLowerCase();

  return (
    <span className={`orders-status status-${normalized}`}>
      {String(status || "Unknown").replaceAll("_", " ")}
    </span>
  );
}