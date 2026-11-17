INSERT INTO users (id, email, password, first_name, last_name, phone, role, enabled, email_verified, created_at, updated_at) VALUES
    (1, 'admin@shop.local', '$2a$10$5RuqsY4ZrxauZwmzo2kjzOWVSjONV9GpCNL0Cw7.Th0L/4EBklEJi', 'Store', 'Admin', '+77000000001', 'ADMIN', TRUE, TRUE, NOW(), NOW()),
    (2, 'manager@shop.local', '$2a$10$0y0pwCQiA6W2qneMkpAnx.mmzHISnCOvUssog3X1SfzHEftEJAakK', 'Order', 'Manager', '+77000000002', 'MANAGER', TRUE, TRUE, NOW(), NOW()),
    (3, 'customer@shop.local', '$2a$10$0mwoYV47ks26m8c5PPo38O/1RmQzhA8hxNzDoPC6K71n6Y.hvZJBO', 'Alice', 'Customer', '+77000000003', 'CUSTOMER', TRUE, TRUE, NOW(), NOW()),
    (4, 'customer2@shop.local', '$2a$10$0mwoYV47ks26m8c5PPo38O/1RmQzhA8hxNzDoPC6K71n6Y.hvZJBO', 'Bob', 'Buyer', '+77000000004', 'CUSTOMER', TRUE, TRUE, NOW(), NOW());

INSERT INTO addresses (id, user_id, title, country, city, street, building, apartment, postal_code, recipient_name, recipient_phone, is_default, latitude, longitude) VALUES
    (1, 3, 'Home', 'Kazakhstan', 'Almaty', 'Abay Avenue', '10', '5', '050000', 'Alice Customer', '+77000000003', TRUE, 43.2389, 76.8897),
    (2, 3, 'Work', 'Kazakhstan', 'Almaty', 'Dostyk Avenue', '52', '12', '050010', 'Alice Customer', '+77000000003', FALSE, NULL, NULL),
    (3, 4, 'Home', 'Kazakhstan', 'Astana', 'Mangilik El Avenue', '8', '40', '010000', 'Bob Buyer', '+77000000004', TRUE, 51.1282, 71.4304);

INSERT INTO brands (id, name, slug, description, logo_url, website_url, country, is_active) VALUES
    (1, 'Apple', 'apple', 'Phones, laptops and audio from Apple', 'https://example.com/logos/apple.png', 'https://www.apple.com', 'USA', TRUE),
    (2, 'Samsung', 'samsung', 'Phones, TVs and accessories from Samsung', 'https://example.com/logos/samsung.png', 'https://www.samsung.com', 'South Korea', TRUE),
    (3, 'Sony', 'sony', 'Audio, TVs and phones from Sony', 'https://example.com/logos/sony.png', 'https://www.sony.com', 'Japan', TRUE);

INSERT INTO categories (id, name, slug, description, image_url, sort_order, is_active, parent_category_id) VALUES
    (1, 'Electronics', 'electronics', 'All electronics', 'https://example.com/categories/electronics.jpg', 1, TRUE, NULL),
    (2, 'Smartphones', 'smartphones', 'Mobile phones', 'https://example.com/categories/smartphones.jpg', 1, TRUE, 1),
    (3, 'Laptops', 'laptops', 'Laptops and notebooks', 'https://example.com/categories/laptops.jpg', 2, TRUE, 1),
    (4, 'Audio', 'audio', 'Headphones and earbuds', 'https://example.com/categories/audio.jpg', 3, TRUE, 1),
    (5, 'TV and Home Cinema', 'tv-and-home-cinema', 'Televisions', 'https://example.com/categories/tv.jpg', 4, TRUE, 1),
    (6, 'Accessories', 'accessories', 'Chargers and cables', 'https://example.com/categories/accessories.jpg', 5, TRUE, 1);

INSERT INTO products (id, name, slug, description, short_description, sku, base_price, discount_price, discount_start_date, discount_end_date, stock_quantity, reserved_quantity, low_stock_threshold, weight, dimensions, is_active, is_featured, is_new_arrival, average_rating, review_count, view_count, sales_count, meta_title, meta_description, created_at, updated_at, brand_id, category_id) VALUES
    (1, 'iPhone 15 128GB', 'iphone-15-128gb', 'iPhone 15 128GB - full description.', 'iPhone 15 128GB from the Apple line', 'APL-IP15-128', 549990, NULL, NULL, NULL, 40, 0, 5, NULL, NULL, TRUE, TRUE, TRUE, 0, 0, 0, 120, 'iPhone 15 128GB', 'Buy iPhone 15 128GB', NOW() - INTERVAL '58 days', NOW(), 1, 2),
    (2, 'iPhone 15 Pro 256GB', 'iphone-15-pro-256gb', 'iPhone 15 Pro 256GB - full description.', 'iPhone 15 Pro 256GB from the Apple line', 'APL-IP15P-256', 729990, 699990, NULL, NULL, 25, 0, 5, NULL, NULL, TRUE, TRUE, FALSE, 0, 0, 0, 85, 'iPhone 15 Pro 256GB', 'Buy iPhone 15 Pro 256GB', NOW() - INTERVAL '56 days', NOW(), 1, 2),
    (3, 'Samsung Galaxy S24 256GB', 'samsung-galaxy-s24-256gb', 'Samsung Galaxy S24 256GB - full description.', 'Samsung Galaxy S24 256GB from the Samsung line', 'SMS-S24-256', 499990, 459990, NULL, NULL, 30, 0, 5, NULL, NULL, TRUE, TRUE, TRUE, 0, 0, 0, 95, 'Samsung Galaxy S24 256GB', 'Buy Samsung Galaxy S24 256GB', NOW() - INTERVAL '54 days', NOW(), 2, 2),
    (4, 'Samsung Galaxy A55 128GB', 'samsung-galaxy-a55-128gb', 'Samsung Galaxy A55 128GB - full description.', 'Samsung Galaxy A55 128GB from the Samsung line', 'SMS-A55-128', 219990, NULL, NULL, NULL, 60, 2, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 140, 'Samsung Galaxy A55 128GB', 'Buy Samsung Galaxy A55 128GB', NOW() - INTERVAL '52 days', NOW(), 2, 2),
    (5, 'Sony Xperia 10 VI', 'sony-xperia-10-vi', 'Sony Xperia 10 VI - full description.', 'Sony Xperia 10 VI from the Sony line', 'SNY-XP10VI', 249990, NULL, NULL, NULL, 15, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 20, 'Sony Xperia 10 VI', 'Buy Sony Xperia 10 VI', NOW() - INTERVAL '50 days', NOW(), 3, 2),
    (6, 'MacBook Air 13 M3', 'macbook-air-13-m3', 'MacBook Air 13 M3 - full description.', 'MacBook Air 13 M3 from the Apple line', 'APL-MBA13-M3', 699990, NULL, NULL, NULL, 20, 1, 5, NULL, NULL, TRUE, TRUE, TRUE, 0, 0, 0, 60, 'MacBook Air 13 M3', 'Buy MacBook Air 13 M3', NOW() - INTERVAL '48 days', NOW(), 1, 3),
    (7, 'MacBook Pro 14 M3', 'macbook-pro-14-m3', 'MacBook Pro 14 M3 - full description.', 'MacBook Pro 14 M3 from the Apple line', 'APL-MBP14-M3', 1099990, 1049990, NULL, NULL, 10, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 25, 'MacBook Pro 14 M3', 'Buy MacBook Pro 14 M3', NOW() - INTERVAL '46 days', NOW(), 1, 3),
    (8, 'Samsung Galaxy Book4 Pro', 'samsung-galaxy-book4-pro', 'Samsung Galaxy Book4 Pro - full description.', 'Samsung Galaxy Book4 Pro from the Samsung line', 'SMS-GB4P', 649990, NULL, NULL, NULL, 12, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 15, 'Samsung Galaxy Book4 Pro', 'Buy Samsung Galaxy Book4 Pro', NOW() - INTERVAL '44 days', NOW(), 2, 3),
    (9, 'Samsung Galaxy Book4 360', 'samsung-galaxy-book4-360', 'Samsung Galaxy Book4 360 - full description.', 'Samsung Galaxy Book4 360 from the Samsung line', 'SMS-GB4-360', 459990, NULL, NULL, NULL, 18, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 12, 'Samsung Galaxy Book4 360', 'Buy Samsung Galaxy Book4 360', NOW() - INTERVAL '42 days', NOW(), 2, 3),
    (10, 'MacBook Air 15 M3', 'macbook-air-15-m3', 'MacBook Air 15 M3 - full description.', 'MacBook Air 15 M3 from the Apple line', 'APL-MBA15-M3', 799990, NULL, NULL, NULL, 8, 0, 5, NULL, NULL, TRUE, FALSE, TRUE, 0, 0, 0, 18, 'MacBook Air 15 M3', 'Buy MacBook Air 15 M3', NOW() - INTERVAL '40 days', NOW(), 1, 3),
    (11, 'AirPods Pro 2', 'airpods-pro-2', 'AirPods Pro 2 - full description.', 'AirPods Pro 2 from the Apple line', 'APL-APP2', 129990, 114990, NULL, NULL, 80, 0, 5, NULL, NULL, TRUE, TRUE, FALSE, 0, 0, 0, 210, 'AirPods Pro 2', 'Buy AirPods Pro 2', NOW() - INTERVAL '38 days', NOW(), 1, 4),
    (12, 'Sony WH-1000XM5', 'sony-wh-1000xm5', 'Sony WH-1000XM5 - full description.', 'Sony WH-1000XM5 from the Sony line', 'SNY-WH1000XM5', 179990, 159990, NULL, NULL, 35, 0, 5, NULL, NULL, TRUE, TRUE, FALSE, 0, 0, 0, 130, 'Sony WH-1000XM5', 'Buy Sony WH-1000XM5', NOW() - INTERVAL '36 days', NOW(), 3, 4),
    (13, 'Sony WF-1000XM5', 'sony-wf-1000xm5', 'Sony WF-1000XM5 - full description.', 'Sony WF-1000XM5 from the Sony line', 'SNY-WF1000XM5', 139990, NULL, NULL, NULL, 25, 0, 5, NULL, NULL, TRUE, FALSE, TRUE, 0, 0, 0, 70, 'Sony WF-1000XM5', 'Buy Sony WF-1000XM5', NOW() - INTERVAL '34 days', NOW(), 3, 4),
    (14, 'Samsung Galaxy Buds3 Pro', 'samsung-galaxy-buds3-pro', 'Samsung Galaxy Buds3 Pro - full description.', 'Samsung Galaxy Buds3 Pro from the Samsung line', 'SMS-BUDS3P', 109990, NULL, NULL, NULL, 40, 1, 5, NULL, NULL, TRUE, FALSE, TRUE, 0, 0, 0, 55, 'Samsung Galaxy Buds3 Pro', 'Buy Samsung Galaxy Buds3 Pro', NOW() - INTERVAL '32 days', NOW(), 2, 4),
    (15, 'Sony Bravia 7 55 inch', 'sony-bravia-7-55', 'Sony Bravia 7 55 inch - full description.', 'Sony Bravia 7 55 inch from the Sony line', 'SNY-BR7-55', 699990, NULL, NULL, NULL, 6, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 9, 'Sony Bravia 7 55 inch', 'Buy Sony Bravia 7 55 inch', NOW() - INTERVAL '30 days', NOW(), 3, 5),
    (16, 'Samsung QLED Q80D 65 inch', 'samsung-qled-q80d-65', 'Samsung QLED Q80D 65 inch - full description.', 'Samsung QLED Q80D 65 inch from the Samsung line', 'SMS-Q80D-65', 799990, 749990, NULL, NULL, 5, 0, 5, NULL, NULL, TRUE, TRUE, FALSE, 0, 0, 0, 11, 'Samsung QLED Q80D 65 inch', 'Buy Samsung QLED Q80D 65 inch', NOW() - INTERVAL '28 days', NOW(), 2, 5),
    (17, 'Samsung Crystal UHD 50 inch', 'samsung-crystal-uhd-50', 'Samsung Crystal UHD 50 inch - full description.', 'Samsung Crystal UHD 50 inch from the Samsung line', 'SMS-CU50', 229990, NULL, NULL, NULL, 0, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 40, 'Samsung Crystal UHD 50 inch', 'Buy Samsung Crystal UHD 50 inch', NOW() - INTERVAL '26 days', NOW(), 2, 5),
    (18, 'Apple 20W USB-C Power Adapter', 'apple-20w-usb-c-adapter', 'Apple 20W USB-C Power Adapter - full description.', 'Apple 20W USB-C Power Adapter from the Apple line', 'APL-ADP20W', 12990, NULL, NULL, NULL, 150, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 300, 'Apple 20W USB-C Power Adapter', 'Buy Apple 20W USB-C Power Adapter', NOW() - INTERVAL '24 days', NOW(), 1, 6),
    (19, 'Samsung 45W Super Fast Charger', 'samsung-45w-charger', 'Samsung 45W Super Fast Charger - full description.', 'Samsung 45W Super Fast Charger from the Samsung line', 'SMS-CHG45W', 14990, NULL, NULL, NULL, 120, 1, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 180, 'Samsung 45W Super Fast Charger', 'Buy Samsung 45W Super Fast Charger', NOW() - INTERVAL '22 days', NOW(), 2, 6),
    (20, 'Universal USB-C Cable 1m', 'universal-usb-c-cable-1m', 'Universal USB-C Cable 1m - full description.', 'Universal USB-C Cable 1m from the generic line', 'GEN-USBC-1M', 3990, NULL, NULL, NULL, 300, 0, 5, NULL, NULL, TRUE, FALSE, FALSE, 0, 0, 0, 400, 'Universal USB-C Cable 1m', 'Buy Universal USB-C Cable 1m', NOW() - INTERVAL '20 days', NOW(), NULL, 6);

INSERT INTO product_images (id, image_url, thumbnail_url, alt_text, is_primary, sort_order, product_id) VALUES
    (1, 'https://example.com/images/iphone-15-128gb.jpg', 'https://example.com/images/thumbs/iphone-15-128gb.jpg', 'iPhone 15 128GB', TRUE, 0, 1),
    (2, 'https://example.com/images/iphone-15-pro-256gb.jpg', 'https://example.com/images/thumbs/iphone-15-pro-256gb.jpg', 'iPhone 15 Pro 256GB', TRUE, 0, 2),
    (3, 'https://example.com/images/samsung-galaxy-s24-256gb.jpg', 'https://example.com/images/thumbs/samsung-galaxy-s24-256gb.jpg', 'Samsung Galaxy S24 256GB', TRUE, 0, 3),
    (4, 'https://example.com/images/samsung-galaxy-a55-128gb.jpg', 'https://example.com/images/thumbs/samsung-galaxy-a55-128gb.jpg', 'Samsung Galaxy A55 128GB', TRUE, 0, 4),
    (5, 'https://example.com/images/sony-xperia-10-vi.jpg', 'https://example.com/images/thumbs/sony-xperia-10-vi.jpg', 'Sony Xperia 10 VI', TRUE, 0, 5),
    (6, 'https://example.com/images/macbook-air-13-m3.jpg', 'https://example.com/images/thumbs/macbook-air-13-m3.jpg', 'MacBook Air 13 M3', TRUE, 0, 6),
    (7, 'https://example.com/images/macbook-pro-14-m3.jpg', 'https://example.com/images/thumbs/macbook-pro-14-m3.jpg', 'MacBook Pro 14 M3', TRUE, 0, 7),
    (8, 'https://example.com/images/samsung-galaxy-book4-pro.jpg', 'https://example.com/images/thumbs/samsung-galaxy-book4-pro.jpg', 'Samsung Galaxy Book4 Pro', TRUE, 0, 8),
    (9, 'https://example.com/images/samsung-galaxy-book4-360.jpg', 'https://example.com/images/thumbs/samsung-galaxy-book4-360.jpg', 'Samsung Galaxy Book4 360', TRUE, 0, 9),
    (10, 'https://example.com/images/macbook-air-15-m3.jpg', 'https://example.com/images/thumbs/macbook-air-15-m3.jpg', 'MacBook Air 15 M3', TRUE, 0, 10),
    (11, 'https://example.com/images/airpods-pro-2.jpg', 'https://example.com/images/thumbs/airpods-pro-2.jpg', 'AirPods Pro 2', TRUE, 0, 11),
    (12, 'https://example.com/images/sony-wh-1000xm5.jpg', 'https://example.com/images/thumbs/sony-wh-1000xm5.jpg', 'Sony WH-1000XM5', TRUE, 0, 12),
    (13, 'https://example.com/images/sony-wf-1000xm5.jpg', 'https://example.com/images/thumbs/sony-wf-1000xm5.jpg', 'Sony WF-1000XM5', TRUE, 0, 13),
    (14, 'https://example.com/images/samsung-galaxy-buds3-pro.jpg', 'https://example.com/images/thumbs/samsung-galaxy-buds3-pro.jpg', 'Samsung Galaxy Buds3 Pro', TRUE, 0, 14),
    (15, 'https://example.com/images/sony-bravia-7-55.jpg', 'https://example.com/images/thumbs/sony-bravia-7-55.jpg', 'Sony Bravia 7 55 inch', TRUE, 0, 15),
    (16, 'https://example.com/images/samsung-qled-q80d-65.jpg', 'https://example.com/images/thumbs/samsung-qled-q80d-65.jpg', 'Samsung QLED Q80D 65 inch', TRUE, 0, 16),
    (17, 'https://example.com/images/samsung-crystal-uhd-50.jpg', 'https://example.com/images/thumbs/samsung-crystal-uhd-50.jpg', 'Samsung Crystal UHD 50 inch', TRUE, 0, 17),
    (18, 'https://example.com/images/apple-20w-usb-c-adapter.jpg', 'https://example.com/images/thumbs/apple-20w-usb-c-adapter.jpg', 'Apple 20W USB-C Power Adapter', TRUE, 0, 18),
    (19, 'https://example.com/images/samsung-45w-charger.jpg', 'https://example.com/images/thumbs/samsung-45w-charger.jpg', 'Samsung 45W Super Fast Charger', TRUE, 0, 19),
    (20, 'https://example.com/images/universal-usb-c-cable-1m.jpg', 'https://example.com/images/thumbs/universal-usb-c-cable-1m.jpg', 'Universal USB-C Cable 1m', TRUE, 0, 20);

INSERT INTO product_variants (id, sku, name, attributes, price, stock_quantity, reserved_quantity, image_url, is_active, product_id) VALUES
    (1, 'APL-IP15-128-BLK', 'iPhone 15 128GB Black', '{"color": "Black", "storage": "128GB"}'::jsonb, NULL, 20, 0, NULL, TRUE, 1),
    (2, 'APL-IP15-128-BLU', 'iPhone 15 128GB Blue', '{"color": "Blue", "storage": "128GB"}'::jsonb, NULL, 15, 0, NULL, TRUE, 1),
    (3, 'APL-IP15-256-BLK', 'iPhone 15 256GB Black', '{"color": "Black", "storage": "256GB"}'::jsonb, 619990, 10, 0, NULL, TRUE, 1),
    (4, 'SMS-S24-256-BLK', 'Galaxy S24 256GB Onyx Black', '{"color": "Onyx Black", "storage": "256GB"}'::jsonb, NULL, 15, 0, NULL, TRUE, 3),
    (5, 'SMS-S24-256-GRY', 'Galaxy S24 256GB Marble Grey', '{"color": "Marble Grey", "storage": "256GB"}'::jsonb, NULL, 15, 0, NULL, TRUE, 3);

INSERT INTO product_specifications (id, name, value, group_name, sort_order, product_id) VALUES
    (1, 'Display', '6.1 inch OLED', 'Screen', 0, 1),
    (2, 'Storage', '128 GB', 'Memory', 1, 1),
    (3, 'Battery', '3349 mAh', 'Power', 2, 1),
    (4, 'Display', '6.2 inch AMOLED', 'Screen', 0, 3),
    (5, 'Storage', '256 GB', 'Memory', 1, 3),
    (6, 'Battery', '4000 mAh', 'Power', 2, 3),
    (7, 'Processor', 'Apple M3', 'Performance', 0, 6),
    (8, 'RAM', '8 GB', 'Memory', 1, 6),
    (9, 'Storage', '256 GB SSD', 'Memory', 2, 6),
    (10, 'Type', 'Over-ear', 'General', 0, 12),
    (11, 'Noise cancelling', 'Yes', 'Audio', 1, 12),
    (12, 'Battery life', '30 hours', 'Power', 2, 12);

INSERT INTO delivery_options (id, name, code, description, base_price, free_threshold, estimated_days_min, estimated_days_max, is_active) VALUES
    (1, 'Courier', 'COURIER', 'Courier delivery to the door', 1500, 50000, 1, 3, TRUE),
    (2, 'Pickup point', 'PICKUP_POINT', 'Collect from a pickup point', 0, NULL, 2, 4, TRUE),
    (3, 'Post office', 'POST_OFFICE', 'Delivery to a post office', 1000, 30000, 5, 10, TRUE),
    (4, 'Express', 'EXPRESS', 'Same or next day delivery', 4000, NULL, 1, 1, TRUE);

INSERT INTO promo_codes (id, code, description, discount_type, discount_value, min_order_amount, max_discount_amount, max_usage_count, current_usage_count, max_usage_per_user, valid_from, valid_to, is_active, created_by) VALUES
    (1, 'WELCOME10', '10% off, at most 30 000', 'PERCENTAGE', 10, NULL, 30000, NULL, 1, NULL, NOW() - INTERVAL '30 days', NOW() + INTERVAL '2 years', TRUE, 'admin@shop.local'),
    (2, 'SAVE5000', '5 000 off orders from 50 000', 'FIXED_AMOUNT', 5000, 50000, NULL, 1000, 0, NULL, NOW() - INTERVAL '30 days', NOW() + INTERVAL '2 years', TRUE, 'admin@shop.local'),
    (3, 'FREESHIP', 'Free shipping on orders from 20 000', 'FREE_SHIPPING', 0, 20000, NULL, NULL, 0, NULL, NOW() - INTERVAL '30 days', NOW() + INTERVAL '2 years', TRUE, 'admin@shop.local'),
    (4, 'AUDIO15', '15% off audio products only', 'PERCENTAGE', 15, NULL, 50000, NULL, 0, NULL, NOW() - INTERVAL '30 days', NOW() + INTERVAL '2 years', TRUE, 'admin@shop.local'),
    (5, 'EXPIRED2025', 'Expired code, used to show that it is refused', 'PERCENTAGE', 10, NULL, NULL, NULL, 0, NULL, NOW() - INTERVAL '400 days', NOW() - INTERVAL '30 days', TRUE, 'admin@shop.local');

INSERT INTO promo_code_categories (promo_code_id, category_id) VALUES
    (4, 4);

INSERT INTO orders (id, order_number, status, country, city, street, building, apartment, postal_code, recipient_name, recipient_phone, latitude, longitude, delivery_method, delivery_tracking_number, estimated_delivery_date, actual_delivery_date, subtotal, shipping_cost, discount, bonus_points_used, bonus_points_earned, total_amount, promo_code, customer_note, admin_note, created_at, updated_at, paid_at, shipped_at, delivered_at, user_id) VALUES
    (1, 'ORD-SEED-0001', 'DELIVERED', 'Kazakhstan', 'Almaty', 'Abay Avenue', '10', '5', '050000', 'Alice Customer', '+77000000003', 43.2389, 76.8897, 'COURIER', 'TRK-SEED-0001', CURRENT_DATE - 20 + 3, CURRENT_DATE - 17, 127980, 0, 0, 0, 0, 127980, NULL, NULL, NULL, NOW() - INTERVAL '20 days', NOW() - INTERVAL '17 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '19 days', NOW() - INTERVAL '17 days', 3),
    (2, 'ORD-SEED-0002', 'SHIPPED', 'Kazakhstan', 'Almaty', 'Abay Avenue', '10', '5', '050000', 'Alice Customer', '+77000000003', 43.2389, 76.8897, 'COURIER', 'TRK-SEED-0002', CURRENT_DATE - 5 + 3, NULL, 159990, 0, 0, 0, 0, 159990, NULL, NULL, NULL, NOW() - INTERVAL '5 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days', NULL, 3),
    (3, 'ORD-SEED-0003', 'PAID', 'Kazakhstan', 'Almaty', 'Dostyk Avenue', '52', '12', '050010', 'Alice Customer', '+77000000003', NULL, NULL, 'COURIER', NULL, CURRENT_DATE - 2 + 3, NULL, 699990, 0, 30000, 0, 0, 669990, 'WELCOME10', NULL, NULL, NOW() - INTERVAL '2 days', NOW() - INTERVAL '0 days', NOW() - INTERVAL '2 days', NULL, NULL, 3),
    (4, 'ORD-SEED-0004', 'PENDING', 'Kazakhstan', 'Almaty', 'Abay Avenue', '10', '5', '050000', 'Alice Customer', '+77000000003', 43.2389, 76.8897, 'PICKUP_POINT', NULL, CURRENT_DATE - 1 + 4, NULL, 439980, 0, 0, 0, 0, 439980, NULL, NULL, NULL, NOW() - INTERVAL '1 days', NOW() - INTERVAL '0 days', NULL, NULL, NULL, 3),
    (5, 'ORD-SEED-0005', 'CONFIRMED', 'Kazakhstan', 'Astana', 'Mangilik El Avenue', '8', '40', '010000', 'Bob Buyer', '+77000000004', 51.1282, 71.4304, 'POST_OFFICE', NULL, CURRENT_DATE - 1 + 10, NULL, 124980, 0, 0, 0, 0, 124980, NULL, NULL, NULL, NOW() - INTERVAL '1 days', NOW() - INTERVAL '0 days', NULL, NULL, NULL, 4),
    (6, 'ORD-SEED-0006', 'CANCELLED', 'Kazakhstan', 'Astana', 'Mangilik El Avenue', '8', '40', '010000', 'Bob Buyer', '+77000000004', 51.1282, 71.4304, 'COURIER', NULL, CURRENT_DATE - 10 + 3, NULL, 249990, 0, 0, 0, 0, 249990, NULL, NULL, NULL, NOW() - INTERVAL '10 days', NOW() - INTERVAL '7 days', NULL, NULL, NULL, 4);

INSERT INTO order_items (id, quantity, price_at_purchase, product_snapshot, order_id, product_id, variant_id) VALUES
    (1, 1, 114990, '{"productId": 11, "name": "AirPods Pro 2", "sku": "APL-APP2", "variant": null, "price": 114990}'::jsonb, 1, 11, NULL),
    (2, 1, 12990, '{"productId": 18, "name": "Apple 20W USB-C Power Adapter", "sku": "APL-ADP20W", "variant": null, "price": 12990}'::jsonb, 1, 18, NULL),
    (3, 1, 159990, '{"productId": 12, "name": "Sony WH-1000XM5", "sku": "SNY-WH1000XM5", "variant": null, "price": 159990}'::jsonb, 2, 12, NULL),
    (4, 1, 699990, '{"productId": 6, "name": "MacBook Air 13 M3", "sku": "APL-MBA13-M3", "variant": null, "price": 699990}'::jsonb, 3, 6, NULL),
    (5, 2, 219990, '{"productId": 4, "name": "Samsung Galaxy A55 128GB", "sku": "SMS-A55-128", "variant": null, "price": 219990}'::jsonb, 4, 4, NULL),
    (6, 1, 109990, '{"productId": 14, "name": "Samsung Galaxy Buds3 Pro", "sku": "SMS-BUDS3P", "variant": null, "price": 109990}'::jsonb, 5, 14, NULL),
    (7, 1, 14990, '{"productId": 19, "name": "Samsung 45W Super Fast Charger", "sku": "SMS-CHG45W", "variant": null, "price": 14990}'::jsonb, 5, 19, NULL),
    (8, 1, 249990, '{"productId": 5, "name": "Sony Xperia 10 VI", "sku": "SNY-XP10VI", "variant": null, "price": 249990}'::jsonb, 6, 5, NULL);

INSERT INTO payments (id, transaction_id, payment_method, status, amount, currency, bonus_points_used, card_last_four_digits, paid_at, error_message, error_code, refunded_amount, metadata, order_id) VALUES
    (1, 'TXN-SEED-0001', 'CARD', 'COMPLETED', 127980, 'KZT', 0, '1111', NOW() - INTERVAL '20 days', NULL, NULL, 0, NULL, 1),
    (2, 'TXN-SEED-0002', 'CARD', 'COMPLETED', 159990, 'KZT', 0, '1111', NOW() - INTERVAL '5 days', NULL, NULL, 0, NULL, 2),
    (3, 'TXN-SEED-0003', 'CARD', 'COMPLETED', 669990, 'KZT', 0, '1111', NOW() - INTERVAL '2 days', NULL, NULL, 0, NULL, 3),
    (4, 'TXN-SEED-0005', 'CARD', 'FAILED', 124980, 'KZT', 0, '0002', NULL, 'Declined: insufficient funds', 'INSUFFICIENT_FUNDS', 0, NULL, 5);

INSERT INTO promo_code_usages (id, discount_applied, used_at, promo_code_id, user_id, order_id) VALUES
    (1, 30000, NOW() - INTERVAL '2 days', 1, 3, 3);

INSERT INTO reviews (id, rating, title, comment, pros, cons, images, is_verified_purchase, helpful_count, not_helpful_count, admin_reply, admin_reply_at, status, created_at, updated_at, product_id, user_id) VALUES
    (1, 5, 'Great noise cancelling', 'Very comfortable and the sound is clear.', NULL, NULL, NULL, TRUE, 0, 0, 'Thank you for your feedback!', NOW(), 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 11, 3),
    (2, 4, 'Does the job', 'Charges my phone fast, a bit expensive.', NULL, NULL, NULL, TRUE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 18, 3),
    (3, 5, 'Love it', 'Camera and battery are excellent.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 1, 3),
    (4, 4, 'Solid phone', 'Bright screen, battery could be better.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 3, 3),
    (5, 5, 'Fast and silent', 'Perfect laptop for university.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 6, 3),
    (6, 4, 'Very good headphones', 'Comfortable for long flights.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 12, 3),
    (7, 4, 'Good earbuds', 'Fit well, case scratches easily.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 11, 4),
    (8, 4, 'Nice upgrade', 'USB-C finally, good camera.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 1, 4),
    (9, 5, 'Best Android phone', 'Smooth and fast.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 3, 4),
    (10, 5, 'Amazing sound', 'Best headphones I have owned.', NULL, NULL, NULL, FALSE, 0, 0, NULL, NULL, 'APPROVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 12, 4);

INSERT INTO wishlists (id, price_at_add, notify_on_price_drop, notify_on_back_in_stock, added_at, user_id, product_id) VALUES
    (1, 729990, TRUE, FALSE, NOW(), 3, 2),
    (2, 1099990, FALSE, FALSE, NOW(), 3, 7);

UPDATE products p
SET average_rating = s.avg_rating,
    review_count   = s.cnt
FROM (SELECT product_id,
             ROUND(AVG(rating)::numeric, 2) AS avg_rating,
             COUNT(*) AS cnt
      FROM reviews
      WHERE status = 'APPROVED'
      GROUP BY product_id) s
WHERE p.id = s.product_id;

SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('addresses', 'id'), (SELECT MAX(id) FROM addresses));
SELECT setval(pg_get_serial_sequence('brands', 'id'), (SELECT MAX(id) FROM brands));
SELECT setval(pg_get_serial_sequence('categories', 'id'), (SELECT MAX(id) FROM categories));
SELECT setval(pg_get_serial_sequence('products', 'id'), (SELECT MAX(id) FROM products));
SELECT setval(pg_get_serial_sequence('product_images', 'id'), (SELECT MAX(id) FROM product_images));
SELECT setval(pg_get_serial_sequence('product_variants', 'id'), (SELECT MAX(id) FROM product_variants));
SELECT setval(pg_get_serial_sequence('product_specifications', 'id'), (SELECT MAX(id) FROM product_specifications));
SELECT setval(pg_get_serial_sequence('delivery_options', 'id'), (SELECT MAX(id) FROM delivery_options));
SELECT setval(pg_get_serial_sequence('promo_codes', 'id'), (SELECT MAX(id) FROM promo_codes));
SELECT setval(pg_get_serial_sequence('orders', 'id'), (SELECT MAX(id) FROM orders));
SELECT setval(pg_get_serial_sequence('order_items', 'id'), (SELECT MAX(id) FROM order_items));
SELECT setval(pg_get_serial_sequence('payments', 'id'), (SELECT MAX(id) FROM payments));
SELECT setval(pg_get_serial_sequence('promo_code_usages', 'id'), (SELECT MAX(id) FROM promo_code_usages));
SELECT setval(pg_get_serial_sequence('reviews', 'id'), (SELECT MAX(id) FROM reviews));
SELECT setval(pg_get_serial_sequence('wishlists', 'id'), (SELECT MAX(id) FROM wishlists));
