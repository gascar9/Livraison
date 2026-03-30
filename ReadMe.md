# Systeme de Gestion de Livraison

Projet Java POO -- ING3 ECE Lyon

Application console interactive avec navigation au clavier (fleches) pour gerer les clients, livreurs, commandes et livraisons.

---

## Prerequis

- **Java 21+** (teste avec OpenJDK 25)
- **IntelliJ IDEA** (ou tout IDE Java)
- **JLine 3.25.1** (bibliotheque pour la navigation fleches dans le terminal)

---

## Installation

### 1. Ouvrir le projet

Ouvrir le dossier `Livraison` dans IntelliJ IDEA.

### 2. Ajouter la dependance JLine

JLine permet la navigation avec les fleches dans le terminal.

**Sans Maven** (projet Java simple) :

1. `File` > `Project Structure` (`Cmd+;` sur Mac)
2. Onglet **Libraries** > cliquer sur **+** > **From Maven...**
3. Chercher `org.jline:jline:3.25.1`
4. Valider > **Apply** > **OK**

**Avec Maven** (`pom.xml`) :

```xml
<dependency>
    <groupId>org.jline</groupId>
    <artifactId>jline</artifactId>
    <version>3.25.1</version>
</dependency>
```

### 3. Configurer le VM option

Java 21+ necessite une option pour autoriser l'acces natif de JLine :

1. `Run` > `Edit Configurations...`
2. Selectionner la configuration **Main**
3. Cliquer sur **Modify options** > **Add VM options**
4. Ajouter : `--enable-native-access=ALL-UNNAMED`

---

## Lancement

### Important : utiliser un vrai terminal

La console Run d'IntelliJ **ne supporte pas** le mode raw de JLine.
Les fleches ne fonctionneront pas en cliquant sur le bouton Run.
Il faut lancer depuis un **vrai terminal**.

### Option A -- Script shell (recommande)

1. Compiler le projet : `Cmd+F9` (Build) dans IntelliJ
2. Lancer depuis le terminal :

```bash
./run.sh
```

### Option B -- Terminal d'IntelliJ

1. Compiler : `Cmd+F9`
2. Ouvrir l'onglet **Terminal** (pas "Run")
3. Lancer :

```bash
cd out/production/Livraison
java --enable-native-access=ALL-UNNAMED \
  -cp ".:$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar" \
  Main
```

### Option C -- Compilation manuelle (sans IntelliJ)

```bash
javac -cp "$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar" \
  -d out/production/Livraison src/*.java
./run.sh
```

---

## Utilisation

### Navigation

```
  +==========================================+
  |  SYSTEME DE LIVRAISON                    |
  +==========================================+

   > Gestion des clients
     Gestion des livreurs
     Gestion des commandes
     Gestion des livraisons
     Statistiques
     Quitter

  [^v] Naviguer  |  [Entree] Valider
```

- **Fleches haut/bas** : naviguer entre les options
- **Entree** : valider la selection
- **Retour** dans chaque sous-menu revient au menu precedent

### Saisie de texte

Quand l'application demande une information (nom, telephone, etc.),
taper le texte puis appuyer sur Entree. Laisser vide pour ne pas modifier
un champ existant.

---

## Fonctionnalites

### Gestion des clients

| Action | Description |
|--------|-------------|
| Ajouter | Saisie du nom, prenom, telephone, email, adresse |
| Supprimer | Par ID, avec confirmation et verification des commandes en cours |
| Modifier | Modification partielle (laisser vide = pas de changement) |
| Rechercher | Par identifiant ou par nom (recherche partielle) |
| Afficher | Liste complete ou triee par nom |

### Gestion des livreurs

| Action | Description |
|--------|-------------|
| Ajouter | Saisie du nom, prenom, telephone, vehicule |
| Supprimer | Par ID, avec confirmation et verification des livraisons en cours |
| Modifier | Modification partielle |
| Rechercher | Par identifiant ou par nom |
| Afficher | Liste complete ou triee par nom |

### Gestion des commandes

| Action | Description |
|--------|-------------|
| Creer | Selection du client par menu fleches, saisie de la description |
| Supprimer | Par ID, avec confirmation et verification des livraisons |
| Modifier statut | EN_ATTENTE > EN_PREPARATION > EN_LIVRAISON > LIVREE |
| Rechercher | Par identifiant ou par description |
| Afficher | Toutes, triees par date, par client, ou en cours de livraison |

### Gestion des livraisons

| Action | Description |
|--------|-------------|
| Affecter | Selection commande + livreur + type (standard/express) + date prevue |
| Terminer | Selection de la livraison en cours, marque comme livree |
| En cours | Liste des livraisons non terminees |
| Historique | Liste des livraisons terminees |

### Statistiques

- Nombre de clients, livreurs, commandes, livraisons
- Nombre de commandes livrees et en cours
- Livreur le plus actif
- Activite par livreur (nombre de livraisons terminees)

---

## Architecture du code

### Diagramme de classes

```
                    +------------------+
                    |    Personne      |  (abstract)
                    +------------------+
                    | # id : int       |
                    | - nom : String   |
                    | - prenom : String|
                    | - telephone : String
                    +------------------+
                    | + afficherDetails()* |
                    | + getId/getNom/etc() |
                    +--------+---------+
                             |
                +------------+------------+
                |                         |
      +---------+--------+     +---------+--------+
      |     Client       |     |     Livreur      |
      +------------------+     +------------------+
      | - email : String |     | - vehicule : String
      | - adresse : String     +------------------+
      +------------------+
```

### Fichiers source

```
src/
  Personne.java          Classe abstraite mere de Client et Livreur.
                         Factorise les attributs communs : id, nom, prenom, telephone.
                         Definit afficherDetails() en methode abstraite.

  Client.java            Herite de Personne. Ajoute email et adresse.
                         ID auto-incremente (compteur statique propre).

  Livreur.java           Herite de Personne. Ajoute vehicule.
                         ID auto-incremente (compteur statique propre).

  StatutCommande.java    Enum avec 4 valeurs :
                         EN_ATTENTE, EN_PREPARATION, EN_LIVRAISON, LIVREE.
                         Chaque valeur a un label affichable ("En attente", etc.).

  TypeLivraison.java     Enum avec 2 valeurs : STANDARD, EXPRESS.

  Commande.java          Represente une commande. Contient une reference vers
                         un Client, une description, une date (LocalDate),
                         et un StatutCommande. ID auto-incremente.

  Livraison.java         Represente une livraison. Lie une Commande a un Livreur.
                         Contient une date prevue, une date reelle (null si en cours),
                         et un TypeLivraison. Passe la commande en EN_LIVRAISON
                         a la creation. terminerLivraison() met la date reelle
                         et passe la commande en LIVREE.

  ServiceLivraison.java  Couche metier. Stocke 4 ArrayList :
                         listeClients, listeLivreurs, listeCommandes, listeLivraisons.
                         Methodes CRUD pour chaque entite + recherche par nom,
                         tri par nom/date, statistiques, verifications avant
                         suppression (cascade). Les getters renvoient des listes
                         non modifiables (Collections.unmodifiableList).

  MenuCLI.java           Couche interface. Encapsule JLine 3.25.1.
                         selectionner() : menu fleches avec boucle de lecture.
                         lireTexte() / lireEntier() : saisie clavier caractere
                         par caractere en mode raw.
                         Methodes d'affichage avec couleurs ANSI.

  Main.java              Point d'entree. Initialise le terminal et les donnees
                         de demo. Contient tous les menus et leurs actions.
                         Delegue la logique metier a ServiceLivraison
                         et l'affichage a MenuCLI.
```

### Relations entre classes

```
Main ----utilise----> MenuCLI          (interface terminal)
Main ----utilise----> ServiceLivraison (logique metier)

ServiceLivraison ----gere----> ArrayList<Client>
ServiceLivraison ----gere----> ArrayList<Livreur>
ServiceLivraison ----gere----> ArrayList<Commande>
ServiceLivraison ----gere----> ArrayList<Livraison>

Commande ----reference----> Client          (le client qui a commande)
Commande ----reference----> StatutCommande  (etat de la commande)

Livraison ----reference----> Commande       (la commande livree)
Livraison ----reference----> Livreur        (le livreur affecte)
Livraison ----reference----> TypeLivraison  (standard ou express)

Client  ----herite----> Personne
Livreur ----herite----> Personne
```

### Concepts POO utilises

| Concept | Ou dans le code |
|---------|-----------------|
| **Heritage** | `Personne` (abstraite) > `Client`, `Livreur` |
| **Abstraction** | `afficherDetails()` est abstraite dans `Personne` |
| **Encapsulation** | Attributs prives, getters/setters, listes non modifiables |
| **Enumerations** | `StatutCommande`, `TypeLivraison` |
| **Collections** | `ArrayList` pour stocker les entites |
| **Relations** | Composition (Commande contient Client), association (Livraison lie Commande et Livreur) |
| **Polymorphisme** | `afficherDetails()` s'execute differemment pour Client et Livreur |

### Fonctionnalites avancees implementees

1. Tri des clients par nom
2. Tri des livreurs par nom
3. Tri des commandes par date
4. Statistiques livreurs les plus actifs
5. Commandes d'un client donne
6. Types de livraison (express / standard)
7. Recherche multicritere (par id, nom, description)

---

## Dependances

| Bibliotheque | Version | Usage |
|---|---|---|
| [JLine](https://github.com/jline/jline3) | 3.25.1 | Navigation fleches, raw mode terminal, lecture clavier |

---

## Troubleshooting

**Les fleches ne marchent pas :**
Lancer depuis l'onglet Terminal d'IntelliJ ou un terminal externe, pas la console Run.

**Warning "restricted method" :**
Ajouter `--enable-native-access=ALL-UNNAMED` dans les VM options.

**Warning "dumb terminal" :**
Normal dans la console Run d'IntelliJ. Utiliser un vrai terminal.

**ClassNotFoundException: org.jline... :**
Le jar JLine n'est pas dans le classpath. Verifier le `-cp` dans la commande ou la config IntelliJ.

**L'application affiche l'ancien code apres modification :**
Recompiler avec `Cmd+F9` dans IntelliJ ou `javac` en ligne de commande avant de relancer `./run.sh`.
