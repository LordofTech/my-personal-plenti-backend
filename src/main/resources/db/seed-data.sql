-- Plenti Backend Comprehensive Seed Data
-- This script populates the database with sample data for testing and development

-- Clean up existing data (optional - comment out for production)
-- DELETE FROM product_variants WHERE 1=1;
-- DELETE FROM product_images WHERE 1=1;
-- DELETE FROM products WHERE 1=1;
-- DELETE FROM categories WHERE 1=1;
-- DELETE FROM stores WHERE 1=1;
-- DELETE FROM promo_codes WHERE 1=1;
-- DELETE FROM users WHERE 1=1;

-- ===== CATEGORIES =====
INSERT INTO categories (name, description, image_url, created_at) VALUES
('Food', 'Fresh food, groceries and everyday essentials', 'https://placehold.co/300x200/orange/white?text=Food', NOW()),
('Beverages', 'Soft drinks, juices, water and more', 'https://placehold.co/300x200/blue/white?text=Beverages', NOW()),
('Personal Care', 'Skin care, hair care and hygiene products', 'https://placehold.co/300x200/green/white?text=Personal+Care', NOW()),
('Beauty', 'Makeup, spa essentials and beauty products', 'https://placehold.co/300x200/pink/white?text=Beauty', NOW()),
('Fashion', 'Clothing, footwear and accessories', 'https://placehold.co/300x200/purple/white?text=Fashion', NOW()),
('Electronics', 'Gadgets, accessories and tech products', 'https://placehold.co/300x200/gray/white?text=Electronics', NOW()),
('Home & Kitchen', 'Home essentials and kitchen items', 'https://placehold.co/300x200/brown/white?text=Home', NOW());

-- ===== STORES =====
INSERT INTO stores (name, location, type, inventory_capacity, latitude, longitude, created_at) VALUES
('Ikeja Dark Store', 'Ikeja, Lagos', 'dark', 1000, 6.5964, 3.3486, NOW()),
('Surulere Partner Store', 'Surulere, Lagos', 'partner', 500, 6.4969, 3.3612, NOW()),
('Victoria Island Dark Store', 'Victoria Island, Lagos', 'dark', 1200, 6.4281, 3.4219, NOW()),
('Lekki Partner Store', 'Lekki Phase 1, Lagos', 'partner', 800, 6.4474, 3.4737, NOW()),
('Yaba Dark Store', 'Yaba, Lagos', 'dark', 900, 6.5074, 3.3723, NOW());

-- ===== PRODUCTS - FOOD CATEGORY =====
INSERT INTO products (name, description, price, category, stock, image_url, category_id, is_clearance, is_freebie, is_featured, bulk_price, average_rating, review_count, last_updated) VALUES
-- Groceries & Staples
('Indomie Instant Noodles - Pack of 5', 'Popular chicken flavored instant noodles', 500.00, 'Food', 200, 'https://placehold.co/400x400/FFA500/white?text=Indomie', 1, false, false, true, 450.00, 4.8, 156, NOW()),
('Golden Penny Spaghetti 500g', 'Premium quality pasta for delicious meals', 450.00, 'Food', 150, 'https://placehold.co/400x400/FFD700/white?text=Golden+Penny', 1, false, false, false, 400.00, 4.5, 89, NOW()),
('Peak Milk Powder 400g', 'Rich and creamy powdered milk', 1200.00, 'Food', 100, 'https://placehold.co/400x400/87CEEB/white?text=Peak+Milk', 1, false, false, true, 1100.00, 4.7, 203, NOW()),
('Dangote Sugar 1kg', 'Pure refined white sugar', 800.00, 'Food', 180, 'https://placehold.co/400x400/FFFFFF/black?text=Dangote+Sugar', 1, false, false, false, 750.00, 4.6, 134, NOW()),
('Golden Penny Semovita 1kg', 'Premium semolina for delicious swallow', 650.00, 'Food', 120, 'https://placehold.co/400x400/FFD700/white?text=Semovita', 1, false, false, false, 600.00, 4.4, 78, NOW()),

-- Rice & Grains
('Royal Stallion Rice 5kg', 'Premium quality parboiled rice', 5200.00, 'Food', 80, 'https://placehold.co/400x400/8B4513/white?text=Royal+Stallion', 1, false, false, true, 5000.00, 4.9, 312, NOW()),
('Mama Gold Rice 5kg', 'Quality parboiled rice for Nigerian meals', 4800.00, 'Food', 90, 'https://placehold.co/400x400/FFD700/black?text=Mama+Gold', 1, false, false, false, 4600.00, 4.6, 189, NOW()),
('Caprice Rice 5kg', 'Premium long grain rice', 5500.00, 'Food', 60, 'https://placehold.co/400x400/FFFFFF/black?text=Caprice+Rice', 1, false, false, false, 5300.00, 4.8, 234, NOW()),

-- Cooking Essentials
('Power Oil Vegetable Oil 3L', 'Pure vegetable cooking oil', 3200.00, 'Food', 110, 'https://placehold.co/400x400/FFFF00/black?text=Power+Oil', 1, false, false, false, 3000.00, 4.5, 167, NOW()),
('Devon Kings Vegetable Oil 4L', 'Premium quality vegetable oil', 4000.00, 'Food', 95, 'https://placehold.co/400x400/FFD700/black?text=Devon+Kings', 1, true, false, false, 3700.00, 4.6, 145, NOW()),
('Maggi Star Cubes 50 Pack', 'Classic seasoning cubes', 350.00, 'Food', 250, 'https://placehold.co/400x400/FF0000/white?text=Maggi', 1, false, false, true, 320.00, 4.7, 289, NOW()),
('Knorr Chicken Cubes 50 Pack', 'Rich chicken flavored seasoning', 380.00, 'Food', 230, 'https://placehold.co/400x400/FFD700/black?text=Knorr', 1, false, false, false, 350.00, 4.6, 256, NOW()),
('Tomato Paste 210g - Gino', 'Rich concentrated tomato paste', 250.00, 'Food', 200, 'https://placehold.co/400x400/FF6347/white?text=Gino+Paste', 1, false, false, false, 230.00, 4.4, 178, NOW()),

-- Bakery & Snacks
('Cabin Biscuits', 'Crunchy cream crackers', 200.00, 'Food', 300, 'https://placehold.co/400x400/D2691E/white?text=Cabin', 1, false, false, false, 180.00, 4.3, 234, NOW()),
('Bournvita 500g', 'Nutritious chocolate malt drink', 1400.00, 'Food', 85, 'https://placehold.co/400x400/8B4513/white?text=Bournvita', 1, false, false, true, 1300.00, 4.8, 198, NOW()),
('Milo 400g', 'Energy drink with malt and cocoa', 1350.00, 'Food', 90, 'https://placehold.co/400x400/228B22/white?text=Milo', 1, false, false, true, 1250.00, 4.7, 221, NOW()),
('Quaker Oats 500g', 'Wholesome breakfast oats', 1100.00, 'Food', 75, 'https://placehold.co/400x400/F5DEB3/black?text=Quaker', 1, false, false, false, 1000.00, 4.5, 156, NOW()),
('Gala Sausage Roll', 'Delicious meat filled pastry', 150.00, 'Food', 400, 'https://placehold.co/400x400/8B4513/white?text=Gala', 1, false, false, true, null, 4.4, 567, NOW()),
('Meat Pie', 'Freshly baked meat pie', 300.00, 'Food', 150, 'https://placehold.co/400x400/D2691E/white?text=Meat+Pie', 1, false, false, false, null, 4.5, 289, NOW()),
('Doughnut - Glazed', 'Sweet glazed doughnut', 200.00, 'Food', 200, 'https://placehold.co/400x400/FFD700/black?text=Doughnut', 1, false, false, false, null, 4.3, 178, NOW());

-- ===== PRODUCTS - BEVERAGES CATEGORY =====
INSERT INTO products (name, description, price, category, stock, image_url, category_id, is_clearance, is_freebie, is_featured, bulk_price, average_rating, review_count, last_updated) VALUES
-- Soft Drinks
('Coca-Cola 50cl', 'Classic refreshing soft drink', 200.00, 'Beverages', 500, 'https://placehold.co/400x400/FF0000/white?text=Coca+Cola', 2, false, false, true, 180.00, 4.7, 789, NOW()),
('Fanta Orange 50cl', 'Refreshing orange flavored drink', 200.00, 'Beverages', 480, 'https://placehold.co/400x400/FFA500/white?text=Fanta', 2, false, false, true, 180.00, 4.6, 654, NOW()),
('Sprite 50cl', 'Crisp lemon-lime drink', 200.00, 'Beverages', 450, 'https://placehold.co/400x400/00FF00/black?text=Sprite', 2, false, false, false, 180.00, 4.6, 598, NOW()),
('Pepsi 50cl', 'Bold and refreshing cola', 200.00, 'Beverages', 400, 'https://placehold.co/400x400/0000FF/white?text=Pepsi', 2, false, false, false, 180.00, 4.5, 423, NOW()),
('7Up 50cl', 'Refreshing lemon-lime soda', 200.00, 'Beverages', 380, 'https://placehold.co/400x400/00FF00/black?text=7Up', 2, false, false, false, 180.00, 4.5, 389, NOW()),

-- Malt Drinks
('Malta Guinness 33cl', 'Non-alcoholic malt drink', 250.00, 'Beverages', 300, 'https://placehold.co/400x400/8B4513/white?text=Malta+Guinness', 2, false, false, true, 230.00, 4.8, 456, NOW()),
('Amstel Malta 33cl', 'Rich malt drink', 250.00, 'Beverages', 280, 'https://placehold.co/400x400/228B22/white?text=Amstel+Malta', 2, false, false, false, 230.00, 4.7, 398, NOW()),
('Supermalt 33cl', 'Premium malt drink', 300.00, 'Beverages', 150, 'https://placehold.co/400x400/8B4513/white?text=Supermalt', 2, false, false, false, 280.00, 4.9, 234, NOW()),

-- Juices & Water
('Hollandia Yoghurt 1L - Strawberry', 'Creamy strawberry yoghurt', 800.00, 'Beverages', 120, 'https://placehold.co/400x400/FF69B4/white?text=Hollandia', 2, false, false, true, 750.00, 4.7, 345, NOW()),
('Chi Exotic 1L - Mango', 'Tropical mango fruit drink', 600.00, 'Beverages', 180, 'https://placehold.co/400x400/FFA500/white?text=Chi+Exotic', 2, false, false, false, 550.00, 4.6, 289, NOW()),
('Five Alive Pulpy Orange 1L', 'Orange juice with pulp', 850.00, 'Beverages', 100, 'https://placehold.co/400x400/FFA500/white?text=Five+Alive', 2, false, false, false, 800.00, 4.8, 267, NOW()),
('Chivita 100% Fruit Juice 1L', 'Pure fruit juice', 900.00, 'Beverages', 95, 'https://placehold.co/400x400/FF6347/white?text=Chivita', 2, false, false, false, 850.00, 4.7, 198, NOW()),
('Eva Water 75cl', 'Pure table water', 100.00, 'Beverages', 600, 'https://placehold.co/400x400/87CEEB/black?text=Eva+Water', 2, false, false, false, 90.00, 4.5, 456, NOW()),
('Nestle Pure Life Water 75cl', 'Quality drinking water', 100.00, 'Beverages', 550, 'https://placehold.co/400x400/87CEEB/black?text=Pure+Life', 2, false, false, false, 90.00, 4.5, 412, NOW());

-- ===== PRODUCTS - PERSONAL CARE CATEGORY =====
INSERT INTO products (name, description, price, category, stock, image_url, category_id, is_clearance, is_freebie, is_featured, bulk_price, average_rating, review_count, last_updated) VALUES
-- Soaps & Body Wash
('Lux Soap - Soft Touch', 'Luxurious bathing soap', 250.00, 'Personal Care', 250, 'https://placehold.co/400x400/FF69B4/white?text=Lux+Soap', 3, false, false, true, 230.00, 4.6, 567, NOW()),
('Dettol Soap - Original', 'Antibacterial protection soap', 300.00, 'Personal Care', 200, 'https://placehold.co/400x400/228B22/white?text=Dettol', 3, false, false, true, 280.00, 4.8, 678, NOW()),
('Imperial Leather Soap', 'Classic bathing soap', 280.00, 'Personal Care', 180, 'https://placehold.co/400x400/FFD700/black?text=Imperial', 3, false, false, false, 260.00, 4.5, 445, NOW()),
('Pears Soap', 'Gentle transparent soap', 320.00, 'Personal Care', 150, 'https://placehold.co/400x400/FFA500/white?text=Pears', 3, false, false, false, 300.00, 4.7, 389, NOW()),

-- Toothpaste & Oral Care
('Close-Up Toothpaste 100ml', 'Fresh breath toothpaste', 400.00, 'Personal Care', 220, 'https://placehold.co/400x400/FF0000/white?text=Close+Up', 3, false, false, true, 380.00, 4.6, 456, NOW()),
('Oral-B Toothpaste 75ml', 'Complete dental care', 450.00, 'Personal Care', 180, 'https://placehold.co/400x400/0000FF/white?text=Oral+B', 3, false, false, false, 420.00, 4.7, 398, NOW()),
('Colgate Toothpaste 100ml', 'Cavity protection toothpaste', 420.00, 'Personal Care', 200, 'https://placehold.co/400x400/FF0000/white?text=Colgate', 3, false, false, false, 400.00, 4.6, 512, NOW()),

-- Skin Care
('Nivea Body Lotion 400ml', 'Moisturizing body lotion', 1200.00, 'Personal Care', 130, 'https://placehold.co/400x400/0000FF/white?text=Nivea', 3, false, false, true, 1100.00, 4.8, 345, NOW()),
('Vaseline Petroleum Jelly 100ml', 'Multi-purpose skin protectant', 500.00, 'Personal Care', 180, 'https://placehold.co/400x400/87CEEB/black?text=Vaseline', 3, false, false, false, 470.00, 4.7, 456, NOW()),
('Fair & White Lotion 500ml', 'Brightening body lotion', 1800.00, 'Personal Care', 90, 'https://placehold.co/400x400/FFFFFF/black?text=Fair+White', 3, false, false, false, 1700.00, 4.5, 234, NOW());

-- Hair Care
('Sunsilk Shampoo 400ml', 'Nourishing hair shampoo', 1100.00, 'Personal Care', 110, 'https://placehold.co/400x400/FF69B4/white?text=Sunsilk', 3, false, false, false, 1000.00, 4.6, 289, NOW()),
('Dark and Lovely Relaxer Kit', 'Hair relaxer system', 1500.00, 'Personal Care', 80, 'https://placehold.co/400x400/8B4513/white?text=Dark+Lovely', 3, false, false, false, 1400.00, 4.7, 198, NOW());

-- ===== PRODUCTS - BEAUTY CATEGORY =====
INSERT INTO products (name, description, price, category, stock, image_url, category_id, is_clearance, is_freebie, is_featured, bulk_price, average_rating, review_count, last_updated) VALUES
('Rexona Deodorant Roll-On', 'Long-lasting freshness', 800.00, 'Beauty', 150, 'https://placehold.co/400x400/87CEEB/black?text=Rexona', 4, false, false, true, 750.00, 4.7, 345, NOW()),
('Sure Deodorant Spray', 'All-day protection', 900.00, 'Beauty', 120, 'https://placehold.co/400x400/FF69B4/white?text=Sure', 4, false, false, false, 850.00, 4.6, 267, NOW()),
('Maybelline Lipstick - Red', 'Classic red lipstick', 1500.00, 'Beauty', 60, 'https://placehold.co/400x400/FF0000/white?text=Maybelline', 4, false, false, true, null, 4.8, 189, NOW()),
('Black Opal Foundation', 'Perfect coverage foundation', 2500.00, 'Beauty', 45, 'https://placehold.co/400x400/8B4513/white?text=Black+Opal', 4, false, false, false, null, 4.9, 156, NOW());

-- ===== PRODUCTS - FASHION CATEGORY =====
INSERT INTO products (name, description, price, category, stock, image_url, category_id, is_clearance, is_freebie, is_featured, bulk_price, average_rating, review_count, last_updated) VALUES
('Men\'s T-Shirt - Black', 'Cotton t-shirt, available in multiple sizes', 2500.00, 'Fashion', 50, 'https://placehold.co/400x400/000000/white?text=T-Shirt', 5, false, false, false, null, 4.5, 89, NOW()),
('Men\'s T-Shirt - White', 'Cotton t-shirt, available in multiple sizes', 2500.00, 'Fashion', 55, 'https://placehold.co/400x400/FFFFFF/black?text=T-Shirt', 5, false, false, false, null, 4.5, 92, NOW()),
('Women\'s Dress - Floral', 'Beautiful floral print dress', 5500.00, 'Fashion', 30, 'https://placehold.co/400x400/FF69B4/white?text=Dress', 5, false, false, true, null, 4.7, 67, NOW()),
('Sneakers - White', 'Comfortable casual sneakers', 8000.00, 'Fashion', 40, 'https://placehold.co/400x400/FFFFFF/black?text=Sneakers', 5, true, false, false, null, 4.6, 112, NOW());

-- ===== PRODUCTS - ELECTRONICS CATEGORY =====
INSERT INTO products (name, description, price, category, stock, image_url, category_id, is_clearance, is_freebie, is_featured, bulk_price, average_rating, review_count, last_updated) VALUES
('Oraimo Earbuds', 'Wireless Bluetooth earbuds', 4500.00, 'Electronics', 80, 'https://placehold.co/400x400/000000/white?text=Earbuds', 6, false, false, true, null, 4.7, 234, NOW()),
('Phone Charger - Fast Charging', 'USB-C fast charger', 2500.00, 'Electronics', 120, 'https://placehold.co/400x400/FFFFFF/black?text=Charger', 6, false, false, false, null, 4.5, 189, NOW()),
('Power Bank 10000mAh', 'Portable charging solution', 6500.00, 'Electronics', 60, 'https://placehold.co/400x400/0000FF/white?text=Power+Bank', 6, false, false, true, null, 4.8, 156, NOW()),
('Phone Case - Universal', 'Protective phone case', 1500.00, 'Electronics', 150, 'https://placehold.co/400x400/000000/white?text=Phone+Case', 6, false, false, false, null, 4.4, 267, NOW());

-- ===== PROMO CODES =====
INSERT INTO promo_codes (code, description, discount_percentage, discount_amount, min_order_amount, max_discount, expiry_date, is_active, usage_limit, times_used, created_at) VALUES
('WELCOME30', 'Welcome discount for new users - 30% off first order', 30.0, null, 1000.00, 5000.00, DATE_ADD(NOW(), INTERVAL 365 DAY), true, null, 0, NOW()),
('PLENTI10', '10% off on any order', 10.0, null, 500.00, 2000.00, DATE_ADD(NOW(), INTERVAL 180 DAY), true, null, 0, NOW()),
('FREESHIP', 'Free delivery on orders above ₦5000', 0.0, 500.00, 5000.00, 500.00, DATE_ADD(NOW(), INTERVAL 90 DAY), true, null, 0, NOW()),
('BULK20', '20% off on bulk orders over ₦50,000', 20.0, null, 50000.00, 15000.00, DATE_ADD(NOW(), INTERVAL 120 DAY), true, null, 0, NOW()),
('WEEKEND15', 'Weekend special - 15% off', 15.0, null, 2000.00, 3000.00, DATE_ADD(NOW(), INTERVAL 60 DAY), true, null, 0, NOW());

-- ===== ADMIN USER =====
-- Password: admin123 (encrypted with BCrypt)
INSERT INTO users (name, email, phone_number, password, referral_code, meta_coins, role, suspended, trust_score, is_guest, created_at) VALUES
('Admin User', 'admin@plenti.ng', '+2348000000000', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN001', 0.0, 'ADMIN', false, 100.0, false, NOW()),
('Test User', 'user@test.com', '+2348012345678', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'TESTUSER', 2000.0, 'USER', false, 100.0, false, NOW());

-- ===== BANNERS =====
INSERT INTO banners (title, description, image_url, link_url, display_order, is_active, start_date, end_date, created_at) VALUES
('Welcome to Plenti!', 'Get 30% off your first order with code WELCOME30', 'https://placehold.co/1200x400/FFA500/white?text=Welcome+30%25+OFF', '/promo', 1, true, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), NOW()),
('Free Delivery', 'Free delivery on orders above ₦5,000', 'https://placehold.co/1200x400/228B22/white?text=FREE+DELIVERY', '/products', 2, true, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), NOW()),
('Fresh Products Daily', 'Quality products delivered in 60 minutes', 'https://placehold.co/1200x400/0000FF/white?text=60+MIN+DELIVERY', '/about', 3, true, NOW(), DATE_ADD(NOW(), INTERVAL 180 DAY), NOW());

-- Summary
SELECT 'Seed data loaded successfully!' AS status,
       (SELECT COUNT(*) FROM categories) AS categories,
       (SELECT COUNT(*) FROM products) AS products,
       (SELECT COUNT(*) FROM stores) AS stores,
       (SELECT COUNT(*) FROM promo_codes) AS promo_codes,
       (SELECT COUNT(*) FROM users) AS users,
       (SELECT COUNT(*) FROM banners) AS banners;
