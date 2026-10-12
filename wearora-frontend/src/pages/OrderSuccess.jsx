import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { CheckCircle2, Package, ArrowRight } from "lucide-react";
import api from "../api/api";
import "./OrderSuccess.css";

export default function OrderSuccess() {
  const { orderId } = useParams();

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function fetchOrder() {
      try {
        const response = await api.get(`/orders/${orderId}`);
        setOrder(response.data);
      } catch (err) {
        setError(
          err.response?.data?.message ||
            "Unable to load your order details."
        );
      } finally {
        setLoading(false);
      }
    }

    fetchOrder();
  }, [orderId]);

  const money = (value) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 2,
    }).format(Number(value || 0));

  if (loading) {
    return <main className="order-success-page">Loading your order...</main>;
  }

  if (error || !order) {
    return (
      <main className="order-success-page">
        <h1>Order details unavailable</h1>
        <p>{error || "We couldn't find this order."}</p>
        <Link to="/orders" className="order-primary-button">
          View my orders
        </Link>
      </main>
    );
  }

  return (
    <main className="order-success-page">
      <section className="order-success-card">
        <div className="order-success-icon">
          <CheckCircle2 size={42} strokeWidth={1.5} />
        </div>

        <p className="order-success-eyebrow">WEARORA / ORDER CONFIRMED</p>
        <h1>It's officially yours.</h1>

        <p className="order-success-message">
          Thank you for shopping with Wearora. Your order has been placed
          successfully.
        </p>

        <div className="order-number">
          <span>ORDER NUMBER</span>
          <strong>#{order.orderId}</strong>
        </div>

        <div className="order-success-details">
          <div>
            <span>Order status</span>
            <strong>{order.status}</strong>
          </div>

          <div>
            <span>Payment method</span>
            <strong>{order.paymentMethod || "COD"}</strong>
          </div>

          <div>
            <span>Payment status</span>
            <strong>{order.paymentStatus || "PENDING"}</strong>
          </div>

          <div>
            <span>Order total</span>
            <strong>{money(order.totalAmount)}</strong>
          </div>
        </div>

        <section className="order-delivery">
          <h2>Delivery details</h2>
          <p>{order.deliveryName}</p>
          <p>{order.deliveryPhone}</p>
          <p>{order.shippingAddress}</p>
        </section>

        <section className="order-purchased-items">
          <h2>
            <Package size={19} />
            Items ordered
          </h2>

          {order.items?.map((item) => (
            <div
              className="order-purchased-item"
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
        </section>

        <div className="order-success-actions">
          <Link to="/orders" className="order-primary-button">
            View my orders <ArrowRight size={17} />
          </Link>

          <Link to="/products" className="order-secondary-button">
            Continue shopping
          </Link>
        </div>
      </section>
    </main>
  );
}