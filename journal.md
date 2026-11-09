# Journal de bord — Projet Java (Jalon 2 & Livraison Finale)

**Binôme :** Lucas & Inès  
**Projet :** Gestion des Collaborateurs et des Tickets (EFREI)

---

## 1. Démarche

Pour ce Jalon 2, nous avons complété la gestion des tickets et sécurisé l'ensemble du cycle de vie de l'application.

### Éléments conservés du TP4
* **Héritage et polymorphisme :** Nous réutilisons la hiérarchie `Collaborateur` (`Programmeur` et `Testeur`). Seul un programmeur peut recevoir un ticket, et seul un testeur peut le valider ou le renvoyer.
* **Exceptions métier :** Nous levons des exceptions (`IllegalArgumentException`, `IllegalStateException`) au lieu d'afficher des erreurs directement dans les services. Les erreurs sont attrapées proprement dans `ConsoleApp`.
* **Architecture en couches :** Séparation claire entre l'interface utilisateur (`ConsoleApp`), les services (`TicketService`, `CollaborateurService`) et la persistance (JPA / Hibernate).

---

## 2. Deux décisions techniques

1. **Méthode transactionnelle générique (`dansUneTransaction`) :**
   Nous avons créé une méthode interne dans nos services qui gère l'ouverture (`begin`), la validation (`commit`) et l'annulation (`rollback`) des transactions JPA. Cela évite d'oublier de fermer l' `EntityManager` et garde le code lisible.

2. **Héritage JPA en table unique (`SINGLE_TABLE`) :**
   Tous les collaborateurs sont stockés dans la même table SQL (`collaborateur`), avec une colonne `metier` pour distinguer les programmeurs des testeurs. C'est simple à gérer et efficace pour les requêtes.

---

## 3. État du projet & Tableau de recette

L'application est terminée et fonctionnelle. Toutes les règles métier et la persistance en base de données fonctionnent correctement.

### Tableau de recette (Jalon 2)

| N° | Scénario de test | Résultat attendu | Constat |
| :--- | :--- | :--- | :--- |
| **1** | Créer un ticket sans titre (vide ou espaces), puis un ticket valide. | Le premier est refusé ; le second est créé « À faire », sans responsable. | **OK** |
| **2** | Assigner un ticket à un programmeur, puis un autre ticket à un testeur. | Le premier est accepté ; le second est refusé. | **OK** |
| **3** | Démarrer un ticket non assigné, puis un ticket assigné. | Refus pour le premier ; le second passe « En cours ». | **OK** |
| **4** | Faire passer « En cours » un 4e ticket d'un programmeur qui en a déjà 3. | Refus ; le ticket reste « À faire ». | **OK** |
| **5** | Tenter de passer un ticket directement de « À faire » ou « En cours » à « Terminé ». | Refus, statut inchangé. | **OK** |
| **6** | Soumettre un ticket à validation, tenter de le valider avec un programmeur, puis avec un testeur ; renvoyer un autre ticket « À valider » en cours. | Refus avec le programmeur ; « Terminé » avec le testeur ; le ticket renvoyé repasse « En cours ». | **OK** |
| **7** | Sur un ticket « Terminé » : modifier le titre, le réassigner. Supprimer un ticket « En cours », puis un ticket « À faire ». | Modification, réassignation et première suppression refusées ; la seconde suppression est acceptée. | **OK** |
| **8** | Arrêter puis relancer l'application ; utiliser un ticket ou un collaborateur inexistant ; saisir du texte quand un nombre est attendu. | Tickets, statuts et assignations retrouvés ; message clair, l'application ne plante pas. | **OK** |

---

## 4. README — Lancer et essayer l'application

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

---

## 5. Contribution de chacun

* **Lucas :**
    * Implémentation du cycle de vie des tickets (`demarrerTicket`, `demanderValidationTicket`, `terminerTicket`, `renvoyerTicket`).
    * Gestion des règles de modification (bloquée si le ticket est « Terminé ») et de suppression (autorisée uniquement si « À faire »).
    * Raccordement et mise à jour des choix du menu console dans `ConsoleApp.java`.

* **Inès :**
    * Implémentation de la requête JPQL pour lister les tickets non assignés (triés par priorité décroissante puis par ID).
    * Création du script SQL d'initialisation (`sql/projet-java.sql`) avec le jeu de données de test complet.
    * Réalisation des tests de recette, vérification des 8 scénarios et rédaction du `JOURNAL.md`.