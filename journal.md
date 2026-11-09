# Journal Projet Java 

---

### 1. Démarche
Nous avons, dans un premier temps, intégré l'entité `Ticket` et ses énumérations (`Priorite`, `Statut`), mis à jour le script SQL de création pour correspondre à notre modèle Java.  

Nous avons ensuite créé un `TicketService` pour isoler la logique liée aux tickets (création, assignation, consultation), séparant ainsi les responsabilités de `CollaborateurService` et de l'interface console `ConsoleApp`.  

Enfin, nous avons implémenté les règles spécifiques (ex: assignation réservée aux programmeurs, statut initial `A_FAIRE`).

#### Éléments conservés du TP4
* Nous réutilisons la hiérarchie `Collaborateur` (`Programmeur` et `Testeur`). Seul un programmeur peut recevoir un ticket, et seul un testeur peut le valider ou le renvoyer.
* Nous levons des exceptions (`IllegalArgumentException`, `IllegalStateException`) au lieu d'afficher des erreurs directement dans les services. Les erreurs sont attrapées proprement dans `ConsoleApp`.
* Séparation claire entre l'interface utilisateur (`ConsoleApp`), les services (`TicketService`, `CollaborateurService`) et la persistance (JPA / Hibernate).

---

## 2. Deux décisions techniques

1. Pour faire avancer un ticket dans son cycle de vie, nous avions choisi au départ d'implémenter une seule et unique méthode 'faireAvancerTicket()'. Dans un souci de lisibilité de code et de qualité nous avons ensuite décidé de séparer les méthodes (une pour démarrer, puis demander validation puis terminer). Cela permettait de séparer les responsabilités et ne pas avoir une seule méthode contenant plein de if/else.
2. Mettre @Enumerated(EnumType.ORDINAL) sur les priorités pour que chaque valeur soit automatiquement associéee à un chiffre en base de données et que le classement par ordre croissant soit beaucoup plus significatif.
   
---

## 3. État du projet & Tableau de recette

L'application est terminée et fonctionnelle. Toutes les règles métier et la persistance en base de données fonctionnent correctement.
Ce qui reste incomplet c'est notamment le fait que l'utilisateur doit rentrer manuellement la priorité au clavier lorsqu'il créé un ticket. Nous avons implémenté un menu qui permet à l'utilisateur de faire un choix num"rique lorsqu'il veut la modifier, mais par souci de temps nous n'avons pas pu l'intégrer partout ou cela aurait été nécessaire.
Le concept de gestion des erreurs et son implémentation à tous les endroits ou nous pensions que c'était nécessaire est ce qui nous a le plus posé de difficulté au cours de ce projet.
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
* Travail collectif :
  * Implémentation du cycle de vie des tickets (`demarrerTicket`, `demanderValidationTicket`, `terminerTicket`, `renvoyerTicket`).
* **Lucas :**
    * Implémentation et gestion des règles de modification (bloquée si le ticket est « Terminé ») et de suppression (autorisée uniquement si « À faire »).
    * Raccordement et mise à jour des choix du menu console dans `ConsoleApp.java`.

* **Inès :**
    * Implémentation de la requête JPQL pour lister les tickets non assignés (triés par priorité décroissante puis par ID).
    * Implémentation des classes d'exceptions.
