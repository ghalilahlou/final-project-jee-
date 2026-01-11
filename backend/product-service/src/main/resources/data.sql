-- Insertion de produits par défaut pour tester l'application
-- Ces données seront chargées automatiquement au démarrage de l'application

-- Catégorie Électronique
INSERT INTO products (sku, name, description, price, stock_quantity, category, created_at, updated_at) VALUES
('ELEC-001', 'MacBook Pro 16"', 'Ordinateur portable haute performance avec puce M3 Pro, 16 Go RAM, 512 Go SSD', 25000.00, 15, 'Électronique', NOW(), NOW()),
('ELEC-002', 'iPhone 15 Pro', 'Smartphone dernière génération avec puce A17 Pro, 256 Go', 12000.00, 30, 'Électronique', NOW(), NOW()),
('ELEC-003', 'AirPods Pro', 'Écouteurs sans fil avec réduction de bruit active et audio spatial', 2800.00, 40, 'Électronique', NOW(), NOW()),
('ELEC-004', 'iPad Air', 'Tablette 10.9 pouces avec puce M1, 64 Go', 5500.00, 25, 'Électronique', NOW(), NOW()),
('ELEC-005', 'Apple Watch Series 9', 'Montre connectée avec GPS et capteur de santé avancé', 3500.00, 20, 'Électronique', NOW(), NOW()),
('ELEC-006', 'Samsung Galaxy S24 Ultra', 'Smartphone Android flagship avec S Pen, 512 Go', 11500.00, 18, 'Électronique', NOW(), NOW()),
('ELEC-007', 'Sony WH-1000XM5', 'Casque audio sans fil avec réduction de bruit premium', 3200.00, 22, 'Électronique', NOW(), NOW()),
('ELEC-008', 'Dell XPS 15', 'Ordinateur portable professionnel Intel i7, 16 Go RAM, 1 To SSD', 18000.00, 12, 'Électronique', NOW(), NOW());

-- Catégorie Mode
INSERT INTO products (sku, name, description, price, stock_quantity, category, created_at, updated_at) VALUES
('MODE-001', 'Veste en Cuir Premium', 'Veste élégante en cuir véritable, style moderne', 2500.00, 20, 'Mode', NOW(), NOW()),
('MODE-002', 'Sac à Dos Deluxe', 'Sac à dos pour ordinateur portable, étanche, multiples compartiments', 500.00, 60, 'Mode', NOW(), NOW()),
('MODE-003', 'Chemise Homme Premium', 'Chemise en coton égyptien, coupe slim fit', 450.00, 35, 'Mode', NOW(), NOW()),
('MODE-004', 'Robe de Soirée Élégante', 'Robe longue en soie, parfaite pour les événements', 1800.00, 15, 'Mode', NOW(), NOW()),
('MODE-005', 'Jeans Designer', 'Jeans premium en denim stretch, coupe moderne', 800.00, 45, 'Mode', NOW(), NOW());

-- Catégorie Sports
INSERT INTO products (sku, name, description, price, stock_quantity, category, created_at, updated_at) VALUES
('SPORT-001', 'Nike Air Max 270', 'Chaussures de sport confortables avec amorti Air Max', 1200.00, 50, 'Sports', NOW(), NOW()),
('SPORT-002', 'Raquette Tennis Wilson Pro', 'Raquette professionnelle pour joueurs avancés', 1500.00, 18, 'Sports', NOW(), NOW()),
('SPORT-003', 'Tapis de Yoga Premium', 'Tapis de yoga antidérapant extra épais', 350.00, 40, 'Sports', NOW(), NOW()),
('SPORT-004', 'Ballon Football Adidas', 'Ballon de football officiel taille 5', 280.00, 55, 'Sports', NOW(), NOW()),
('SPORT-005', 'Kit Haltères Ajustables', 'Set d\'haltères de 5 à 25 kg avec support', 2200.00, 12, 'Sports', NOW(), NOW());

-- Catégorie Maison
INSERT INTO products (sku, name, description, price, stock_quantity, category, created_at, updated_at) VALUES
('MAISON-001', 'Canapé Design 3 Places', 'Canapé moderne en tissu premium, très confortable', 8000.00, 10, 'Maison', NOW(), NOW()),
('MAISON-002', 'Lampe LED Intelligente', 'Lampe de bureau avec contrôle via app, RGB', 350.00, 35, 'Maison', NOW(), NOW()),
('MAISON-003', 'Robot Aspirateur', 'Aspirateur robot intelligent avec navigation laser', 2800.00, 15, 'Maison', NOW(), NOW()),
('MAISON-004', 'Set Vaisselle Porcelaine', 'Service de table 24 pièces en porcelaine fine', 1200.00, 25, 'Maison', NOW(), NOW()),
('MAISON-005', 'Cafetière Espresso Pro', 'Machine à café professionnelle avec broyeur intégré', 4500.00, 8, 'Maison', NOW(), NOW());

-- Catégorie Beauté
INSERT INTO products (sku, name, description, price, stock_quantity, category, created_at, updated_at) VALUES
('BEAUTE-001', 'Parfum Luxe 100ml', 'Eau de parfum premium notes florales et boisées', 800.00, 45, 'Beauté', NOW(), NOW()),
('BEAUTE-002', 'Set Soin Visage', 'Coffret complet: nettoyant, sérum, crème hydratante', 650.00, 30, 'Beauté', NOW(), NOW()),
('BEAUTE-003', 'Palette Maquillage Pro', 'Palette 48 ombres à paupières haute pigmentation', 420.00, 28, 'Beauté', NOW(), NOW());

-- Catégorie Livres
INSERT INTO products (sku, name, description, price, stock_quantity, category, created_at, updated_at) VALUES
('LIVRE-001', 'Best-seller Roman 2026', 'Roman captivant, prix littéraire 2026', 150.00, 100, 'Livres', NOW(), NOW()),
('LIVRE-002', 'Guide Développement Spring Boot', 'Manuel complet pour maîtriser Spring Boot et microservices', 380.00, 50, 'Livres', NOW(), NOW()),
('LIVRE-003', 'Collection Fantasy 3 Tomes', 'Trilogie épique fantastique, édition collector', 450.00, 35, 'Livres', NOW(), NOW());
