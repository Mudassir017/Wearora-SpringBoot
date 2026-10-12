import { useEffect, useState } from "react";
import { MapPin, Plus, Trash2, Check, RotateCcw } from "lucide-react";
import api from "../api/api";
import "./Addresses.css";

const initialForm = {
  fullName: "",
  phone: "",
  addressLine1: "",
  addressLine2: "",
  city: "",
  state: "",
  postalCode: "",
  country: "India",
  isDefault: true,
};

export default function Addresses() {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [form, setForm] = useState(initialForm);

  async function loadAddresses() {
    try {
      setError("");
      const response = await api.get("/addresses");
      setAddresses(response.data || []);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Could not load saved addresses."
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAddresses();
  }, []);

  function handleChange(event) {
    const { name, value, type, checked } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: type === "checkbox" ? checked : value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setError("");

    try {
      await api.post("/addresses", form);
      setForm(initialForm);
      await loadAddresses();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Could not save address. Please try again."
      );
    } finally {
      setSaving(false);
    }
  }

  async function makeDefault(id) {
    setError("");

    try {
      await api.patch(`/addresses/${id}/default`);
      await loadAddresses();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Could not update default address."
      );
    }
  }

  async function deleteAddress(id) {
    if (!window.confirm("Are you sure you want to delete this address?")) {
      return;
    }

    setError("");

    try {
      await api.delete(`/addresses/${id}`);
      await loadAddresses();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Could not delete address."
      );
    }
  }

  return (
    <main className="addresses-page">
      <header className="addresses-heading">
        <p className="addresses-eyebrow">WEARORA / MY ACCOUNT</p>

        <h1>My Addresses</h1>

        <p>
          Your destinations, saved for a smoother checkout.
        </p>
      </header>

      {error && (
        <p className="addresses-error" role="alert">
          {error}
        </p>
      )}

      <section className="addresses-section">
        <div className="addresses-section-heading">
          <div>
            <p className="addresses-eyebrow">DELIVERY DETAILS</p>
            <h2>Add a new address</h2>
          </div>

          <span className="addresses-heading-icon">
            <Plus size={21} />
          </span>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="addresses-form-grid">
            <div className="addresses-field">
              <label htmlFor="fullName">Full name</label>
              <input
                id="fullName"
                name="fullName"
                placeholder="Enter recipient's name"
                value={form.fullName}
                onChange={handleChange}
                maxLength={100}
                autoComplete="name"
                required
              />
            </div>

            <div className="addresses-field">
              <label htmlFor="phone">Phone number</label>
              <input
                id="phone"
                name="phone"
                type="tel"
                placeholder="Enter phone number"
                value={form.phone}
                onChange={handleChange}
                maxLength={15}
                autoComplete="tel"
                required
              />
            </div>

            <div className="addresses-field addresses-field-full">
              <label htmlFor="addressLine1">
                House number / Street / Area
              </label>
              <input
                id="addressLine1"
                name="addressLine1"
                placeholder="House no., street, locality"
                value={form.addressLine1}
                onChange={handleChange}
                maxLength={255}
                autoComplete="address-line1"
                required
              />
            </div>

            <div className="addresses-field addresses-field-full">
              <label htmlFor="addressLine2">
                Landmark / Apartment (optional)
              </label>
              <input
                id="addressLine2"
                name="addressLine2"
                placeholder="Apartment, floor, landmark"
                value={form.addressLine2}
                onChange={handleChange}
                maxLength={255}
                autoComplete="address-line2"
              />
            </div>

            <div className="addresses-field">
              <label htmlFor="city">City</label>
              <input
                id="city"
                name="city"
                placeholder="Enter city"
                value={form.city}
                onChange={handleChange}
                maxLength={100}
                autoComplete="address-level2"
                required
              />
            </div>

            <div className="addresses-field">
              <label htmlFor="state">State</label>
              <input
                id="state"
                name="state"
                placeholder="Enter state"
                value={form.state}
                onChange={handleChange}
                maxLength={100}
                autoComplete="address-level1"
                required
              />
            </div>

            <div className="addresses-field">
              <label htmlFor="postalCode">PIN code</label>
              <input
                id="postalCode"
                name="postalCode"
                placeholder="Enter PIN code"
                value={form.postalCode}
                onChange={handleChange}
                maxLength={20}
                autoComplete="postal-code"
                required
              />
            </div>

            <div className="addresses-field">
              <label htmlFor="country">Country</label>
              <input
                id="country"
                name="country"
                placeholder="Enter country"
                value={form.country}
                onChange={handleChange}
                maxLength={100}
                autoComplete="country-name"
                required
              />
            </div>
          </div>

          <label className="addresses-default-toggle">
            <input
              type="checkbox"
              name="isDefault"
              checked={form.isDefault}
              onChange={handleChange}
            />

            <span className="def">
              <strong id="def">Set as default delivery address</strong>
              <small>
                Use this address as your preferred delivery destination.
              </small>
            </span>
          </label>

          <div className="addresses-actions">
            <button
              type="submit"
              disabled={saving}
              className="addresses-primary-button"
            >
              {saving ? (
                "Saving address..."
              ) : (
                <>
                  <Plus size={17} />
                  {form.isDefault
                    ? "Save as Default Address"
                    : "Save Address"}
                </>
              )}
            </button>

            <button
              type="button"
              onClick={() => setForm(initialForm)}
              className="addresses-secondary-button"
            >
              <RotateCcw size={15} />
              Clear form
            </button>
          </div>
        </form>
      </section>

      <section className="addresses-saved-section">
        <div className="addresses-section-heading">
          <div>
            <p className="addresses-eyebrow">YOUR DETAILS</p>
            <h2>Saved addresses</h2>
          </div>

          <span className="addresses-count">
            {addresses.length}{" "}
            {addresses.length === 1 ? "ADDRESS" : "ADDRESSES"}
          </span>
        </div>

        {loading ? (
          <div className="addresses-empty">
            Loading your saved addresses...
          </div>
        ) : addresses.length === 0 ? (
          <div className="addresses-empty">
            <MapPin size={30} strokeWidth={1.4} />
            <h3>No saved addresses yet</h3>
            <p>
              Add your first delivery address using the form above.
            </p>
          </div>
        ) : (
          <div className="addresses-list">
            {addresses.map((address) => (
              <article
                key={address.addressId}
                className="addresses-card"
              >
                <div className="addresses-card-top">
                  <div className="addresses-recipient">
                    <span className="addresses-card-icon">
                      <MapPin size={19} />
                    </span>

                    <h3>{address.fullName}</h3>
                  </div>

                  {address.default && (
                    <span className="addresses-default-badge">
                      <Check size={12} />
                      Default
                    </span>
                  )}
                </div>

                <div className="addresses-card-details">
                  <p>{address.addressLine1}</p>

                  {address.addressLine2 && (
                    <p>{address.addressLine2}</p>
                  )}

                  <p>
                    {[address.city, address.state]
                      .filter(Boolean)
                      .join(", ")}
                    {address.postalCode
                      ? ` ${address.postalCode}`
                      : ""}
                  </p>

                  <p>{address.country}</p>

                  <p className="addresses-card-phone">
                    {address.phone}
                  </p>
                </div>

                <div className="addresses-card-actions">
                  {!address.default && (
                    <button
                      type="button"
                      onClick={() => makeDefault(address.addressId)}
                      className="addresses-text-button"
                    >
                      Set as default
                    </button>
                  )}

                  <button
                    type="button"
                    onClick={() => deleteAddress(address.addressId)}
                    className="addresses-text-button addresses-delete-button"
                  >
                    <Trash2 size={14} />
                    Delete address
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}