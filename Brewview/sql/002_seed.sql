BEGIN;

-- Users
INSERT INTO users (username, email, password_hash, role_name) VALUES
("admin_user", "admin@example.com", "$2y$10$3ChKZYFPnEXinPo8nfcn6O0PyU2h.tMsFAEuu9W2RpsF/cNUxI/5.", "ROLE_ADMIN"),
("alex_rodgers", "alex@example.com", "$2y$10$1yOuXGBsNf9OzFkcwR.mBuaTLRnqIFBss0Z7KAuxp2EzFui.rrm3.", "ROLE_USER"),
("jane_smith", "jane@example.com", "$2y$10$Yss7uzma2yAqEHrMCdvw3u.lwM0DwutO0CGLhkqSaAKVShyXka/uK", "ROLE_USER");

-- Locations
INSERT INTO locations (location_name, address, city, country) VALUES
("The Hoppy Place", "123 Main St", "Portland", "USA"),
("Brew Masters Pub", "456 Oak Ave", "Denver", "USA"),
("The Tap Room", "789 Elm Street", "Austin", "USA"),
("Barrel & Vine", "321 Maple Dr", "San Diego", "USA"),
("Downtown Drafts", "654 Pine Blvd", "Seattle", "USA");

-- Beverages
INSERT INTO beverages (beverage_name, type, abv, description, image_url) VALUES
("Hazy IPA", "Beer", 6.5, "A juicy and hazy New England style IPA with notes of citrus and tropical fruit", "https://example.com/images/hazy-ipa.jpg"),
("Stout Noir", "Beer", 8.2, "Rich and creamy imperial stout with chocolate and coffee flavors", "https://example.com/images/stout.jpg"),
("Summer Lager", "Beer", 4.8, "Crisp and refreshing pilsner-style lager perfect for warm days", "https://example.com/images/lager.jpg"),
("Amber Ale", "Beer", 5.5, "Smooth amber ale with caramel malt sweetness and balanced hop bitterness", "https://example.com/images/amber.jpg"),
("Sour Cherry Wheat", "Beer", 5.0, "Tart wheat beer with real cherry puree, refreshing and fruity", "https://example.com/images/sour.jpg"),
("West Coast IPA", "Beer", 7.0, "Classic bitter IPA with pine and grapefruit hop character", "https://example.com/images/west-coast-ipa.jpg"),
("Belgian Dubbel", "Beer", 7.5, "Dark Belgian ale with notes of raisin, plum, and spice", "https://example.com/images/dubbel.jpg"),
("Pale Ale", "Beer", 5.2, "Easy-drinking pale ale with floral hops and biscuit malt", "https://example.com/images/pale-ale.jpg");

-- Reviews
INSERT INTO reviews (user_id, beverage_id, location_id, rating, notes) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'a8098c1a-f86e-11da-bd1a-00112444be1e', 4.5, 'Absolutely loved this! The tropical notes really shine through. Will definitely order again.'),
('550e8400-e29b-41d4-a716-446655440000', '7c9e6679-7425-40de-944b-e07fc1f90ae7', '6fa459ea-ee8a-3ca4-894e-db77e160355e', 4.0, 'Perfect summer beer. Very refreshing and clean finish.'),
('6fa459ea-ee8a-3ca4-894e-db77e160355e', '16fd2706-8baf-433b-82eb-8c7fada847da', 'a8098c1a-f86e-11da-bd1a-00112444be1e', 5.0, 'Best stout Ive had in years. The chocolate flavor is incredible.'),
('6fa459ea-ee8a-3ca4-894e-db77e160355e', '886313e1-3b8a-5372-9b90-0c9aee199e5d', '123e4567-e89b-12d3-a456-426614174000', 3.5, 'Good beer but a bit too sweet for my taste. Still enjoyable though.'),
('16fd2706-8baf-433b-82eb-8c7fada847da', '550e8400-e29b-41d4-a716-446655440000', 'c9bf9e57-1685-4c89-bafb-ff5af830be8a', 4.0, 'Solid hazy IPA. Not too bitter, nice balance.'),
('6fa459ea-ee8a-3ca4-894e-db77e160355e', '1c6b1470-5a6b-4f20-930d-7b7a1c9d9f12', '6fa459ea-ee8a-3ca4-894e-db77e160355e', 4.5, 'Love the tartness! Really unique and refreshing.'),
('16fd2706-8baf-433b-82eb-8c7fada847da', '9b2d1f50-8f8f-4d9d-9f70-1b3d6e1a6b34', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 3.0, 'Too bitter for me, but I can appreciate the quality.'),
('550e8400-e29b-41d4-a716-446655440000', 'd9428888-122b-11e1-b85c-61cd3cbb3210', '123e4567-e89b-12d3-a456-426614174000', 4.5, 'Complex and delicious. The Belgian yeast character is perfect.'),
('6fa459ea-ee8a-3ca4-894e-db77e160355e', '3c0e6b8e-fc32-4c4a-8f35-2f0b3d0f7f9d', 'c9bf9e57-1685-4c89-bafb-ff5af830be8a', 4.0, 'Great session beer. Easy to drink and flavorful.'),
('16fd2706-8baf-433b-82eb-8c7fada847da', '7c9e6679-7425-40de-944b-e07fc1f90ae7', 'a8098c1a-f86e-11da-bd1a-00112444be1e', 5.0, 'This is my go-to beer. Can''t go wrong with it.'),
('16fd2706-8baf-433b-82eb-8c7fada847da', '16fd2706-8baf-433b-82eb-8c7fada847da', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 4.5, 'Rich and smooth. Pairs great with dessert.'),
('550e8400-e29b-41d4-a716-446655440000', '9b2d1f50-8f8f-4d9d-9f70-1b3d6e1a6b34', '6fa459ea-ee8a-3ca4-894e-db77e160355e', 3.5, 'Decent IPA but I prefer hazier styles.');

COMMIT;