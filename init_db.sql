-- ============================================================
-- Script d'initialisation de la base de données
-- Projet : WashFlow - Gestion d'une Station de Lavage Auto
-- ============================================================

DROP DATABASE IF EXISTS station_lavage;
CREATE DATABASE station_lavage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE station_lavage;

-- ============================================================
-- Table : utilisateur (pour le Login)
-- ============================================================
CREATE TABLE utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin'
);

INSERT INTO utilisateur (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('employe', 'employe123', 'employe');

-- ============================================================
-- Table : proprietaire (informations sur le propriétaire du véhicule)
-- ============================================================
CREATE TABLE proprietaire (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    telephone VARCHAR(20),
    type_proprietaire VARCHAR(20) NOT NULL DEFAULT 'Particulier'  -- Particulier / Professionnel
);

-- ============================================================
-- Table : vehicule (1ère entité principale)
-- ============================================================
CREATE TABLE vehicule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    immatriculation VARCHAR(20) NOT NULL UNIQUE,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50),
    couleur VARCHAR(30),
    type_vehicule VARCHAR(20) NOT NULL,         -- Berline / SUV / Camion / Moto
    proprietaire_id INT,
    actif BOOLEAN DEFAULT TRUE,
    date_enregistrement DATE NOT NULL,
    nombre_lavages INT DEFAULT 0,
    remise DOUBLE DEFAULT 0,                     -- Slider remise %
    remarques TEXT,
    FOREIGN KEY (proprietaire_id) REFERENCES proprietaire(id) ON DELETE SET NULL
);

-- ============================================================
-- Table : lavage (2ème entité principale, liée à vehicule)
-- ============================================================
CREATE TABLE lavage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicule_id INT NOT NULL,
    type_lavage VARCHAR(20) NOT NULL,            -- Basic / Premium / Complet
    date_lavage DATE NOT NULL,
    duree INT NOT NULL,                          -- en minutes
    prix DOUBLE NOT NULL,
    etat VARCHAR(20) DEFAULT 'En attente',        -- En attente / En cours / Terminé
    paye BOOLEAN DEFAULT FALSE,
    employe VARCHAR(50),
    remarques TEXT,
    FOREIGN KEY (vehicule_id) REFERENCES vehicule(id) ON DELETE CASCADE
);

-- ============================================================
-- Données de test
-- ============================================================
INSERT INTO proprietaire (nom, prenom, telephone, type_proprietaire) VALUES
('Alami', 'Karim', '0661234567', 'Particulier'),
('Bensaid', 'Sara', '0662345678', 'Professionnel'),
('Tazi', 'Mehdi', '0663456789', 'Particulier'),
('Ouazzani', 'Lina', '0664567890', 'Professionnel'),
('Berrada', 'Yassine', '0665678901', 'Particulier'),
('Idrissi', 'Nadia', '0666789012', 'Particulier'),
('El Fassi', 'Omar', '0667890123', 'Professionnel');

INSERT INTO vehicule (immatriculation, marque, modele, couleur, type_vehicule, proprietaire_id, actif, date_enregistrement, nombre_lavages, remise, remarques) VALUES
('AB-123-CD', 'Peugeot', '308', 'Noir', 'Berline', 1, TRUE, '2024-01-15', 5, 5, 'Client fidèle'),
('EF-456-GH', 'Renault', 'Kadjar', 'Blanc', 'SUV', 2, TRUE, '2024-02-20', 3, 0, ''),
('IJ-789-KL', 'Mercedes', 'Classe C', 'Gris', 'Berline', 3, FALSE, '2024-03-10', 1, 0, 'Inactif depuis mars'),
('MN-012-OP', 'BMW', 'X5', 'Bleu', 'SUV', 4, TRUE, '2024-03-25', 8, 10, 'VIP'),
('QR-345-ST', 'Audi', 'A4', 'Rouge', 'Berline', 5, TRUE, '2024-04-05', 2, 0, ''),
('UV-678-WX', 'Dacia', 'Duster', 'Vert', 'SUV', 6, TRUE, '2024-04-18', 1, 0, ''),
('YZ-901-AB', 'Toyota', 'Corolla', 'Blanc', 'Berline', 7, TRUE, '2024-05-02', 4, 5, '');

INSERT INTO lavage (vehicule_id, type_lavage, date_lavage, duree, prix, etat, paye, employe, remarques) VALUES
(1, 'Premium', '2024-06-10', 45, 150, 'Terminé', TRUE, 'Karim', ''),
(2, 'Complet', '2024-06-11', 90, 280, 'En cours', FALSE, 'Sara', ''),
(3, 'Basic', '2024-06-12', 20, 80, 'En attente', FALSE, 'Karim', ''),
(4, 'Premium', '2024-06-13', 50, 160, 'Terminé', TRUE, 'Sara', ''),
(5, 'Complet', '2024-06-14', 85, 270, 'Terminé', TRUE, 'Karim', ''),
(6, 'Basic', '2024-06-15', 25, 85, 'En attente', FALSE, 'Sara', ''),
(7, 'Premium', '2024-06-16', 55, 165, 'En cours', FALSE, 'Karim', '');
