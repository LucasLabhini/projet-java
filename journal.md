### 1. Démarche
Nous avons, dans un premier temps, intégré l'entité `Ticket` et ses énumérations (`Priorite`, `Statut`), mis à jour le script SQL de création pour correspondre à notre modèle Java.  
Nous avons ensuite créé un `TicketService` pour isoler la logique liée aux tickets (création, assignation, consultation), séparant ainsi les responsabilités de `CollaborateurService` et de l'interface console `ConsoleApp`.  
Enfin, nous avons implémenté les règles spécifiques (ex: assignation réservée aux programmeurs, statut initial `A_FAIRE`).

### 2. Les éléments conservés du TP4  
Nous avons conservé l'héritage et le polymorphisme entre collaborateur, programmeur et testeur qui nous a servi notamment pour accepter l'assignation d'un ticket à un programmeur mais la refuser à un testeur.
Nous avons gardé le principe des exceptions métiers au lieu des "System.out.println". Chaque opération interdite lève une exception qui est interceptée puis gérée par l'interface.  


## Tableau d'autocontrôle (Jalon 1)

| N° | Autocontrôle du jalon 1 | Résultat attendu | Constat |
| :--- | :--- | :--- | :--- |
| **J1** | Créer deux tickets de priorités différentes, puis consulter les tickets. | Ils apparaissent « À faire », sans responsable. | OK : Ticket #1 : Imprimer (BASSE) - A_FAIRE - Responsable : aucun |
| **J2** | Assigner un ticket à un programmeur, puis afficher les tickets de ce programmeur. | Le ticket apparaît dans sa liste. | OK |
| **J3** | Arrêter puis relancer l'application, consulter les tickets. | Tickets, statuts et assignations retrouvés à l'identique. | OK |