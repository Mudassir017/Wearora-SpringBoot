-- Run once against the existing Wearora MySQL database before deploying code that uses roles.
-- Verify the first query returns no rows. Abort and correct data if it does.
SELECT variantId, stockQuantity
FROM product_variants
WHERE stockQuantity < 0;

ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER';

ALTER TABLE product_variants
    ADD CONSTRAINT chk_product_variants_stock_nonnegative
    CHECK (stockQuantity >= 0);

-- Promote a deliberately selected existing account; public registration always creates CUSTOMER accounts.
-- UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
