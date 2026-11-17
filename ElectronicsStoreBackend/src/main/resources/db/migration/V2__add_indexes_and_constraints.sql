CREATE INDEX idx_categories_parent ON categories(parent_category_id);
CREATE INDEX idx_carts_user ON carts(user_id);
CREATE INDEX idx_carts_session ON carts(session_id);
CREATE INDEX idx_cart_items_variant ON cart_items(variant_id);
CREATE INDEX idx_order_items_variant ON order_items(variant_id);
CREATE INDEX idx_promo_code_usages_order ON promo_code_usages(order_id);
CREATE INDEX idx_promo_code_categories_category ON promo_code_categories(category_id);
CREATE INDEX idx_promo_code_products_product ON promo_code_products(product_id);

CREATE INDEX idx_products_base_price ON products(base_price);
CREATE INDEX idx_products_created_at ON products(created_at);
CREATE INDEX idx_products_sales_count ON products(sales_count);
CREATE INDEX idx_products_average_rating ON products(average_rating);
CREATE INDEX idx_products_featured ON products(is_featured);
CREATE INDEX idx_products_new_arrival ON products(is_new_arrival);
CREATE INDEX idx_categories_sort_order ON categories(sort_order);
CREATE INDEX idx_brands_active ON brands(is_active);
CREATE INDEX idx_reviews_status ON reviews(status);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_refresh_tokens_expiry ON refresh_tokens(expiry_date);

ALTER TABLE reviews ADD CONSTRAINT uq_reviews_user_product UNIQUE (user_id, product_id);
ALTER TABLE wishlists ADD CONSTRAINT uq_wishlists_user_product UNIQUE (user_id, product_id);

ALTER TABLE reviews ADD CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5);
ALTER TABLE cart_items ADD CONSTRAINT ck_cart_items_quantity CHECK (quantity > 0);
ALTER TABLE order_items ADD CONSTRAINT ck_order_items_quantity CHECK (quantity > 0);
ALTER TABLE products ADD CONSTRAINT ck_products_stock CHECK (stock_quantity >= 0 AND reserved_quantity >= 0);
ALTER TABLE products ADD CONSTRAINT ck_products_price CHECK (base_price >= 0);
