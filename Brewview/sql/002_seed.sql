BEGIN;

-- Users
INSERT INTO users (username, email, password, role) VALUES
('admin_user', 'admin@example.com', '$2y$10$3ChKZYFPnEXinPo8nfcn6O0PyU2h.tMsFAEuu9W2RpsF/cNUxI/5.', 'ROLE_ADMIN'),
('alex_rodgers', 'alex@example.com', '$2y$10$1yOuXGBsNf9OzFkcwR.mBuaTLRnqIFBss0Z7KAuxp2EzFui.rrm3.', 'ROLE_USER'),
('jane_smith', 'jane@example.com', '$2y$10$Yss7uzma2yAqEHrMCdvw3u.lwM0DwutO0CGLhkqSaAKVShyXka/uK', 'ROLE_USER');

-- Locations
INSERT INTO locations (location_name, address, city, country) VALUES
('The Hoppy Place', '123 Main St', 'Portland', 'USA'),
('Brew Masters Pub', '456 Oak Ave', 'Denver', 'USA'),
('The Tap Room', '789 Elm Street', 'Austin', 'USA'),
('Barrel & Vine', '321 Maple Dr', 'San Diego', 'USA'),
('Downtown Drafts', '654 Pine Blvd', 'Seattle', 'USA');

-- Beverages
INSERT INTO beverages (beverage_name, type, abv, description, image_url) VALUES
('Hazy IPA', 'Beer', 6.5, 'A juicy and hazy New England style IPA with notes of citrus and tropical fruit', 'https://example.com/images/hazy-ipa.jpg'),
('Stout Noir', 'Beer', 8.2, 'Rich and creamy imperial stout with chocolate and coffee flavors', 'https://example.com/images/stout.jpg'),
('Summer Lager', 'Beer', 4.8, 'Crisp and refreshing pilsner-style lager perfect for warm days', 'https://example.com/images/lager.jpg'),
('Amber Ale', 'Beer', 5.5, 'Smooth amber ale with caramel malt sweetness and balanced hop bitterness', 'https://example.com/images/amber.jpg'),
('Sour Cherry Wheat', 'Beer', 5.0, 'Tart wheat beer with real cherry puree, refreshing and fruity', 'https://example.com/images/sour.jpg'),
('West Coast IPA', 'Beer', 7.0, 'Classic bitter IPA with pine and grapefruit hop character', 'https://example.com/images/west-coast-ipa.jpg'),
('Belgian Dubbel', 'Beer', 7.5, 'Dark Belgian ale with notes of raisin, plum, and spice', 'https://example.com/images/dubbel.jpg'),
('Pale Ale', 'Beer', 5.2, 'Easy-drinking pale ale with floral hops and biscuit malt', 'https://example.com/images/pale-ale.jpg');

-- Reviews
INSERT INTO reviews (user_id, beverage_id, location_id, rating, notes) VALUES
(1, 1, 1, 4.5, "Absolutely loved this! The tropical notes really shine through. Will definitely order again."),
(1, 3, 2, 4.0, "Perfect summer beer. Very refreshing and clean finish."),
(2, 2, 1, 5.0, "Best stout Ive had in years. The chocolate flavor is incredible."),
(2, 4, 3, 3.5, "Good beer but a bit too sweet for my taste. Still enjoyable though."),
(3, 1, 4, 4.0, "Solid hazy IPA. Not too bitter, nice balance."),
(2, 5, 2, 4.5, "Love the tartness! Really unique and refreshing."),
(3, 6, 5, 3.0, "Too bitter for me, but I can appreciate the quality."),
(1, 7, 3, 4.5, "Complex and delicious. The Belgian yeast character is perfect."),
(2, 8, 4, 4.0, "Great session beer. Easy to drink and flavorful."),
(3, 3, 1, 5.0, "This is my go-to beer. Can't go wrong with it."),
(3, 2, 5, 4.5, "Rich and smooth. Pairs great with dessert."),
(1, 6, 2, 3.5, "Decent IPA but I prefer hazier styles.");

COMMIT;