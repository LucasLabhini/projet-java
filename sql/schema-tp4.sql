-- Schéma de RÉFÉRENCE (correction) : équivalent à ce que Hibernate génère
-- avec hibernate.hbm2ddl.auto=update. Cible : MariaDB / MySQL (XAMPP).
CREATE DATABASE IF NOT EXISTS efrei_tp4 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE efrei_tp4;

CREATE TABLE IF NOT EXISTS adresse (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    rue         VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10)  NOT NULL,
    ville       VARCHAR(60)  NOT NULL,
    pays        VARCHAR(60)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS collaborateur (
    identifiant     VARCHAR(10) NOT NULL,
    metier          VARCHAR(40) NOT NULL,   -- discriminant : 'Programmeur' ou 'Testeur'
    prenom          VARCHAR(50) NOT NULL,
    nom             VARCHAR(50) NOT NULL,
    salaire         DOUBLE      NOT NULL,
    langage_prefere VARCHAR(40) NULL,       -- NULL pour un Testeur
    adresse_id      BIGINT      NOT NULL,
    PRIMARY KEY (identifiant),
    CONSTRAINT fk_collaborateur_adresse FOREIGN KEY (adresse_id) REFERENCES adresse (id)
);

CREATE TABLE IF NOT EXISTS ticket (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    titre       VARCHAR(255) DEFAULT NULL,
    priorite    VARCHAR(50)  DEFAULT NULL,
    statut      VARCHAR(50)  DEFAULT NULL,
    assigne_id  VARCHAR(10)  DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ticket_collaborateur FOREIGN KEY (assigne_id) REFERENCES collaborateur (identifiant)
    );

-- Adresses
INSERT INTO adresse (id, rue, code_postal, ville, pays) VALUES
(1, '12 Rue de la Paix', '75002', 'Paris', 'France'),
(2, '8 Avenue des Fleurs', '69001', 'Lyon', 'France'),
(3, '5 Place du Capitole', '31000', 'Toulouse', 'France'),
(4, '14 Boulevard Haussmann', '75009', 'Paris', 'France'),
(5, '3 Rue Royale', '44000', 'Nantes', 'France'),
(6, '20 Quai des Chartrons', '33000', 'Bordeaux', 'France');

-- Collaborateurs
INSERT INTO collaborateur (identifiant, metier, prenom, nom, salaire, langage_prefere, adresse_id) VALUES
('C001', 'Programmeur', 'Alice', 'Martin', 42000, 'Java', 1),
('C002', 'Programmeur', 'Alex', 'Dupont', 44500, 'Python', 2),
('C003', 'Testeur', 'Chloé', 'Bernard', 39000, NULL, 3),
('C004', 'Programmeur', 'Nora', 'Diallo', 47000, 'Java', 4),
('C005', 'Testeur', 'Hugo', 'Petit', 41000, NULL, 5),
('C006', 'Programmeur', 'Lina', 'Moreau', 45500, 'Kotlin', 6);

-- Tickets
INSERT INTO ticket (titre, priorite, statut, assigne_id) VALUES
('Corriger l export PDF', 'HAUTE', 'A_FAIRE', NULL),
('Ajouter la recherche par nom', 'NORMALE', 'A_FAIRE', NULL),
('Mettre a jour la documentation', 'BASSE', 'EN_COURS', 'C001'),
('Optimiser les requetes SQL', 'HAUTE', 'EN_COURS', 'C002'),
('Tester l interface utilisateur', 'NORMALE', 'A_VALIDER', 'C004'),
('Corriger le bug de connexion', 'BASSE', 'TERMINE', 'C006');