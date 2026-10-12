# Wearora

Wearora is a full-stack e-commerce application for browsing apparel, selecting size-based variants, managing a cart and wishlist, saving delivery addresses, and placing cash-on-delivery orders.

## Architecture

| Layer | Technology | Responsibility |
| --- | --- | --- |
| Frontend | React 19, Vite, Axios | Product discovery, authenticated shopping flows, checkout, profile and order screens |
| API | Java 21, Spring Boot, Spring MVC | REST endpoints, validation, checkout orchestration, error responses |
| Security | Spring Security, JWT, BCrypt | Stateless authentication; public catalog reads and protected customer actions |
| Persistence | Spring Data JPA, MySQL | Product catalog, variants, carts, wishlists, addresses, orders and order items |

The frontend lives in [`wearora-frontend`](wearora-frontend) and calls the API at `http://localhost:8080/api` during local development.

## Implemented features

- Registration and login with JWT authentication and BCrypt password hashing.
- Public product catalog with search, category, price-range, pagination, sorting, product details and stock-aware variants.
- Authenticated cart and wishlist flows.
- Saved addresses and a default-address workflow.
- Server-calculated COD checkout: price snapshots are stored on order items and cart data is cleared after a successful order.
- Order history, customer-scoped order details, and cancellation for eligible states.
- Checkout/cancellation stock protection using transactional pessimistic row locks. Checkout serializes access to a cart and locks variants in a stable ID order; cancellation locks the order and its variants, preventing a double restock.

## Prerequisites

- Java 21
- MySQL 8.0.16+ (required for enforced `CHECK` constraints)
- Node.js 20+ and npm

## Database and environment setup

Create a MySQL database named `wearora` (or use a database name of your choice) and set these environment variables before starting the backend:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/wearora"
$env:DB_USERNAME = "your_mysql_user"
$env:DB_PASSWORD = "your_mysql_password"
$env:JWT_SECRET = "a-base64-encoded-secret-with-at-least-256-bits"
```

`DB_URL` defaults to `jdbc:mysql://localhost:3306/wearora`, and `JWT_EXPIRATION_MS` defaults to `3600000` (one hour). `DB_PASSWORD` and `JWT_SECRET` are deliberately required rather than committed to source control.

This project currently uses Hibernate schema updates (`spring.jpa.hibernate.ddl-auto=update`); it does not yet use Flyway or Liquibase migrations. For a production deployment, introduce versioned migrations before relying on automatic schema updates.

After Hibernate has created the tables for a new database, and before deploying the role/stock constraint update to an existing database, run
[`database/mysql/V20261012_01__add_user_role_and_prevent_negative_stock.sql`](database/mysql/V20261012_01__add_user_role_and_prevent_negative_stock.sql)
once. It backfills users as `CUSTOMER`, adds a MySQL-enforced non-negative stock check, and contains the explicit
admin-promotion statement to review and run for a chosen account. Do not use the commented example email unchanged.

## Run locally

Start the backend:

```powershell
.\mvnw.cmd spring-boot:run
```

Start the frontend in a second terminal:

```powershell
cd wearora-frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## Key API routes

| Area | Routes |
| --- | --- |
| Authentication | `POST /api/auth/register`, `POST /api/auth/login` |
| Catalog | `GET /api/products`, `GET /api/products/{id}`, `GET /api/categories` |
| Cart | `GET /api/cart`, `POST /api/cart/items`, `PUT /api/cart/items/{itemId}`, `DELETE /api/cart/items/{itemId}` |
| Wishlist | `GET /api/wishlist`, `POST /api/wishlist/items`, `DELETE /api/wishlist/items/{itemId}` |
| Addresses | `GET/POST /api/addresses`, `PUT/DELETE /api/addresses/{addressId}`, `PATCH /api/addresses/{addressId}/default` |
| Orders | `POST /api/orders/checkout`, `GET /api/orders`, `GET /api/orders/{orderId}`, `PATCH /api/orders/{orderId}/cancel` |

Use `Authorization: Bearer <token>` for authenticated routes. Product mutations (`POST`, `PUT`, and `DELETE /api/products`) require a JWT with the `ADMIN` role. Public registration always creates `CUSTOMER` accounts; promote an intentionally chosen existing account through the reviewed MySQL migration.

## Validation

Run the backend suite:

```powershell
.\mvnw.cmd clean test
```

Run frontend checks:

```powershell
cd wearora-frontend
npm run lint
npm run build
```

The backend tests use an isolated H2 in-memory database. They cover context loading, insufficient checkout stock, order ownership, cancellation stock restoration, and prevention of a second cancellation restock. They do not prove MySQL lock behavior: add MySQL Testcontainers concurrency tests when Docker is available.

## Screenshots

Add verified local screenshots here before publishing the portfolio repository:

- `docs/screenshots/home.png` — storefront/product discovery
- `docs/screenshots/product-details.png` — variant selection
- `docs/screenshots/checkout.png` — COD checkout
- `docs/screenshots/orders.png` — order history and status

## Resume-ready summary

- Built Wearora, a Spring Boot and React e-commerce application with JWT-authenticated customer flows for catalog browsing, carts, wishlists, saved addresses, and COD orders.
- Implemented server-side checkout pricing, immutable order-item price snapshots, transactional inventory decrements, and cancellation-based stock restoration.
- Strengthened order consistency with pessimistic row locking for carts, order cancellation, and stock variants; added isolated automated tests for inventory and ownership safeguards.

## Interview talking points

**Why is checkout price calculated on the server?** The client is treated as untrusted; the service loads the current product price or discount price and persists that unit price on each order item for historical accuracy.

**How do you prevent overselling?** The checkout transaction locks the cart and requested variant rows, verifies stock while those locks are held, then decrements stock. Variant IDs are locked in sorted order to reduce cross-cart deadlock risk.

**How do you prevent duplicate stock restoration?** Cancellation locks the customer’s order first. After the first transaction marks it `CANCELLED`, any later request observes that state and is rejected before variants are changed.

**What would you improve next?** Add Flyway-backed schema management, MySQL Testcontainers concurrent integration tests, payment processing, and observability/audit logs.
