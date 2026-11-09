### Prérequis
* Java JDK 17 ou supérieur
* Base de données MariaDB / MySQL (ex: XAMPP sur le port 3306)
* Maven 3.x

### 1. Préparer la base de données
1. Démarrer le service MySQL dans XAMPP.
2. Importer le fichier `sql/projet-java.sql` (via phpMyAdmin ou en ligne de commande).
   *(Le script crée la base `projet-java`, les tables `adresse`, `collaborateur`, `ticket` et insère 6 collaborateurs et 6 tickets).*

### 2. Lancer l'application
Ouvrir un terminal à la racine du projet et exécuter :
```bash
mvn compile exec:java
```