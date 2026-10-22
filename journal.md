# TP4

## Partie A -Fiabiliser l'application

### 1\. Casser l'application

Comportement 1 : Lorsque l'on souhaite augmenter le salaire avec un pourcentage négatif, cela laisse le salaire de base sans le modifier ni afficher de message d'erreur explicite. Le code devrait afficher un message à l'utilisateur lui disant que la saisie est incorrecte.



Comportement 2 : Lorsque l'on souhaite rechercher un collaborateur par nom, l'application nous laisse saisir des chiffres pour ensuite nous dire qu'aucun collaborateur n'a été trouvé. Il serait plus pertinent de ne pas accepter de chiffres dans la recherche par nom.



Une erreur de saisie et une règle métier violée ne sont pas le même problème. Une erreur de saisie se traite au niveau de l'utilisateur directement en lui disant que sa saisie est incorrecte tandis qu'une règle métier violée est un problème qui se règle dans le code directement. 



### 3\. Un projet que l'on sait reconstruire

Lorsque l'on déclare une bibliothèque dans le pom.xml, Maven s'occupe automatiquement de chercher et installer toutes les "sous-dépendances" de celle-ci. Si on ajoute les fichiers .jar, cela nécessitera d'ajouter toutes les "sous-dépendances" à la main. De plus, les fichiers .jar peuvent être relativement lourds.

## Partie B - Alice et Alex refusent de disparaître

### 5\. Alice refuse de disparître
- Un objet Java est un objet que l'on crée avec un 'new'. Il disparaît dès lors que l'on arrête le programme.
- Une entité JPA est un objet Java mais qui est marqué par l'annotation @Entity. Il rempli donc une ligne d'une table de la base de données et il est possible de modifier l'entité en Java pour que JPA la traduise automatiquement en SQL et fasse la mise à jour en base.
- Enfin, une ligne d'une table c'est la donnée qui est stockée dans le SGBD et qui persiste même après l'arrêt du programme mais on ne peut "y toucher" qu'en SQL.

Concernant la question de si on ne devait faire qu'une seule table pour toute la hiérarchie ou une pour chaque hiérarchie, nous avons privilégié la première solution en ajoutant une sorte de colonne qui précise le type de collaborateur. Cela permet de faire des requêtes beaucoup plus simples sur tous les collaborateurs sans nécessiter de jointure.

Si l'on arrête MySQL et qu'on relance le programme, on obtient un message d'erreur plutôt destiné au développeur dans la console qui dit que la connexion à la base de données n'a pas pu être établie. Rien n'est affiché pour l'utilisateur.

find(C999) retourne : Exception in thread "main" java.lang.NullPointerException: Cannot invoke "fr.efrei.java.Collaborateur.getPrenom()" because "inconnu" is null
at fr.efrei.java.DemarrageJPA.main(DemarrageJPA.java:25).
Soit un message d'erreur disant qu'on ne peut pas récupérer le prénom de quelqu'un (ou quelque chose) dont la valeur est "null" .


### 6\. Alice rentre dans la base

#####  Que vaut une clé comme C001 ?
- On ne peut enregistrer que 999 collaborateurs. Au delà, il y aura un problème si la longueur de l'Id est limitée à 4 caractères.
- C'est la personne qui crée manuellement le collaborateur, qui lui affecte un identifiant. Si 2 personnes créent des collaborateurs en même temps et tentent de leur donner le même Id, le code laissera faire mais la base de données le refusera la deuxième création car elle donnera lieu à un doublon.
- Une erreur commise lors de la création de l'id peut nécessiter de multiples modifications pour la corriger si la clé est référencée dans plusieurs tables.

Lorsque l'on persiste deux collaborateurs différents portant le même identifiant, l'application lève une exception car MariaDB refuse la création d'un doublon sur une clé primaire. Cela se fait lors de l'appel du transaction.commit().
5\. Retrouvez la règle du doublon (mission 2). Avant de persister, comment savoir si C001 existe déjà ? La clé primaire de la base protège-t-elle suffisamment l’utilisateur ?

Avec un .find() nous pouvons savoir si un identifiant existe déjà. Non car MariaDB empêchera la création du nouveau collaborateur mais cela fera crasher l'application complète. Il faut une condition directement dans le code. 

Une clé générée automatiquement dans la base de données serait plus appropriée.


### 7\. Alice obtient une augmentation sans écrire UPDATE

- Le SQL UPDATE est généré par Hibernate qui l'envoie automatiquement à MariaDB en arrière-plan.

Une entité gérée est un objet Java qui est rattaché à un EntityManager ouvert. Parce qu'elle est "gérée", l'outil observe tous les changements qu'elle subit.

Le contexte de persistance, c'est la mémoire de travail de l'EntityManager 

La transaction est le déclencheur de la synchronisation. La base de données ne peut être modifiée qu'à l'intérieur d'une transaction sécurisée. Sans appel à commit() pour valider la transaction, le contexte de persistance n'enverra jamais ses modifications.

-Sortez l’appel de augmenterSalaire après la fin du bloc try (l’EntityManager est alors fermé) : l’entité est détachée. La base change-t-elle ? Non, elle ne change pas. Dès que l'on sort du try, l'entityManager se ferme.
- Le salaire ne change pas car l'exception est levée avant le commit, on se retrouve alors directement dans le bloc catch.


### 8. Retrouver autrement que par identifiant

- Le SQL travaille sur des tables et des colonnes physiques de la base de données. Le JPQL travaille sur des entités et des attributs Java (avec respect de la casse des classes et des champs).
- Une requête JPQL sur `Collaborateur` retourne directement des objets Java typés avec leur type réel (`Programmeur` ou `Testeur`).

### 9\. Alice a toujours une adresse

- Un collaborateur a une adresse, mais plusieurs collaborateurs peuvent partager la même adresse postale (relation Many-to-One). Une adresse peut exister indépendamment d'un collaborateur.
- C'est la table `Collaborateur` qui porte la colonne `adresse_id` référençant la clé `id` de la table `Adresse`.
- `@ManyToOne(cascade = CascadeType.PERSIST)` avec `@JoinColumn(name = "adresse_id")` sur l'attribut `adresse`. Le cascade permet de persister l'adresse automatiquement en même temps que le collaborateur.
- La clé technique auto-générée (`IDENTITY`) ne dépend pas des données métier, ne change jamais et évite les conflits d'insertion ou les mises à jour en cascade.

### 10\. Comprendre d’abord. Automatiser ensuite.

Comparaison entre l'entité générée par l'outil et notre entité manuelle :
- L'outil a mappé la colonne sous forme d'une simple chaîne sans `@GeneratedValue` (`idCollaborateur`), car la base ne gère pas de séquence automatique sur ce format textuel.
- Absence de polymorphisme : L'outil génère un modèle plat avec un attribut `metier` (String) au lieu d'une hiérarchie objet (`Collaborateur`, `Programmeur`, `Testeur`).
- Encapsulation : L'outil crée des setters publics génériques pour chaque colonne, ce qui expose l'état interne et ne permet pas d'appliquer les règles métier (comme empêcher un salaire négatif).
- Attributs spécifiques :`langagePrefere` se retrouve dans la classe unique alors qu'il ne concerne en réalité que les programmeurs.