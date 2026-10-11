# TP4

## Partie A -Fiabiliser l'application

1. ### \- Casser l'application

Comportement 1 : Lorsque l'on souhaite augmenter le salaire avec un pourcentage négatif, cela laisse le salaire de base sans le modifier ni afficher de message d'erreur explicite. Le code devrait afficher un message à l'utilisateur lui disant que la saisie est incorrecte.



Comportement 2 : Lorsque l'on souhaite rechercher un collaborateur par nom, l'application nous laisse saisir des chiffres pour ensuite nous dire qu'aucun collaborateur n'a été trouvé. Il serait plus pertinent de ne pas accepter de chiffres dans la recherche par nom.



Une erreur de saisie et une règle métier violée ne sont pas le même problème. Une erreur de saisie se traite au niveau de l'utilisateur directement en lui disant que sa saisie est incorrecte tandis qu'une règle métier violée est un problème qui se règle dans le code directement. 



### 3\. Un projet que l'on sait reconstruire

Lorsque l'on déclare une bibliothèque dans le pom.xml, Maven s'occupe automatiquement de chercher et installer toutes les "sous-dépendances" de celle-ci. Si on ajoute les fichiers .jar, cela nécessitera d'ajouter toutes les "sous-dépendances" à la main. De plus, les fichiers .jar peuvent être relativement lourds.



### 5\.

5\. Retrouvez la règle du doublon (mission 2). Avant de persister, comment savoir si C001 existe déjà ? La clé primaire de la base protège-t-elle suffisamment l’utilisateur ?

Avec un .find()? Non car MariaDB empêchera la création du nouveau collaborateur mais cela fera crasher l'application complète. Il faut une condition directement dans le code. 





### 6\. A reecrire (ça vient de l'IA ça)

Le format « C + 3 chiffres » (limites et C1000) :

On ne peut enregistrer que 999 collaborateurs (de C001 à C999). Arrivé à C1000, le format est cassé (5 caractères au lieu de 4). Si la base de données a été configurée avec un VARCHAR(4), l'application plantera net car le texte sera trop long.



Qui décide et concurrence sur C007 :

C'est l'utilisateur (humain) qui le tape au clavier. Si deux secrétaires valident la création de C007 exactement en même temps, le premier enregistrement va réussir. Pour le deuxième, MariaDB va bloquer l'insertion et renvoyer une erreur violente de type "Duplicate Entry" (violation de contrainte d'unicité) qui fera crasher le programme du deuxième utilisateur.



Le coût de corriger C01 en C001 avec des clés étrangères :

C'est très coûteux et complexe. Si l'identifiant est la clé primaire, il est utilisé comme référence dans toutes les autres tables (Adresse, Contrat, Congés...). Modifier la clé primaire exige de modifier en cascade toutes les lignes correspondantes dans les autres tables, sous peine de détruire l'intégrité de la base.



Persister deux collaborateurs avec le même ID (que se passe-t-il et quand ?) :

L'application va lever une exception (EntityExistsException ou une erreur SQL de type ConstraintViolationException). Cela se produit exactement au moment du transaction.commit(), car c'est à cet instant précis que l'ordre SQL INSERT est réellement envoyé et vérifié par MariaDB.



Qu’est-ce qui rendrait une clé primaire plus « propre » ?

La solution est d'utiliser une clé technique (générée automatiquement par la base de données, comme un identifiant numérique 1, 2, 3... avec @GeneratedValue). Cette clé est invisible pour l'utilisateur et ne change jamais. Le vrai matricule "C001" devient alors une simple colonne de texte (avec une règle @Column(unique=true)) qu'on peut modifier librement sans casser les relations entre les tables. (Tu feras ça dans le Challenge B du TP !)

