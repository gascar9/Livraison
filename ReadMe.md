# 🚚 Système de Gestion de Livraison

Projet Java POO — ING3 ECE Lyon

Application console interactive avec navigation au clavier (flèches ↑↓) pour gérer les clients, livreurs, commandes et livraisons.

---

## Prérequis

- **Java 21+** (testé avec OpenJDK 25)
- **IntelliJ IDEA**
- **Maven** (intégré dans IntelliJ)

---

## Installation

### 1. Cloner / ouvrir le projet

Ouvrir le dossier `Livraison` dans IntelliJ IDEA.

### 2. Ajouter la dépendance JLine

JLine est la bibliothèque qui permet la navigation avec les flèches dans le terminal.

**Avec Maven** (`pom.xml`) :

```xml
<dependency>
    <groupId>org.jline</groupId>
    <artifactId>jline</artifactId>
    <version>3.25.1</version>
</dependency>
```

Puis : clic droit sur `pom.xml` → **Maven** → **Reload Project**

**Sans Maven** (projet Java simple) :

1. `File` → `Project Structure` (`Cmd+;` sur Mac)
2. Onglet **Libraries** → cliquer sur **+** → **From Maven...**
3. Chercher `org.jline:jline:3.25.1`
4. Valider → **Apply** → **OK**

### 3. Configurer le VM option

Java 21+ nécessite une option pour autoriser l'accès natif de JLine :

1. `Run` → `Edit Configurations...`
2. Sélectionner la configuration **Main**
3. Cliquer sur **Modify options** → **Add VM options**
4. Dans le champ VM options, ajouter :

```
--enable-native-access=ALL-UNNAMED
```

---

## Lancement

### ⚠️ Important : Terminal requis

La console Run intégrée d'IntelliJ **ne supporte pas** le mode raw de JLine (les flèches ne fonctionneront pas). Il faut lancer depuis un **vrai terminal**.

### Option A — Terminal intégré d'IntelliJ (recommandé)

1. Compiler le projet : `Cmd+F9` (Build)
2. Ouvrir l'onglet **Terminal** en bas d'IntelliJ (pas "Run", mais "Terminal")
3. Lancer :

```bash
cd out/production/Livraison
java --enable-native-access=ALL-UNNAMED \
  -cp ".:$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar" \
  Main
```

### Option B — Script shell (plus pratique)

1. Créer un fichier `run.sh` à la racine du projet :

```bash
#!/bin/bash
cd "$(dirname "$0")/out/production/Livraison"
java --enable-native-access=ALL-UNNAMED \
  -cp ".:$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar" \
  Main
```

2. Le rendre exécutable :

```bash
chmod +x run.sh
```

3. Configurer dans IntelliJ :
    - `Run` → `Edit Configurations...` → **+** → **Shell Script**
    - Script path : sélectionner `run.sh`
    - Cocher **Execute in the terminal**

4. Lancer avec le bouton ▶️ (penser à Build avec `Cmd+F9` avant)

### Option C — Alias terminal (pour la soutenance)

Ajouter un alias permanent dans le terminal :

```bash
echo 'alias livraison="cd ~/Livraison/out/production/Livraison && java --enable-native-access=ALL-UNNAMED -cp \".:$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar\" Main"' >> ~/.zshrc
source ~/.zshrc
```

Ensuite, taper simplement `livraison` dans n'importe quel terminal.

---

## Utilisation

```
  ╔══════════════════════════════════════╗
  ║     🚚  SYSTÈME DE LIVRAISON  🚚     ║
  ╚══════════════════════════════════════╝

   ▸ 👤  Gestion des clients        ← sélectionné
     🏍   Gestion des livreurs
     📦  Gestion des commandes
     🚚  Gestion des livraisons
     ❌  Quitter

  ↑↓ Naviguer  │  Entrée Valider
```

- **↑ / ↓** : naviguer entre les options
- **Entrée** : valider la sélection
- L'option **← Retour** dans chaque sous-menu revient au menu précédent

---

## Structure du projet

```
Livraison/
├── src/
│   ├── Client.java            # Entité client
│   ├── Livreur.java           # Entité livreur
│   ├── Commande.java          # Entité commande
│   ├── Livraison.java         # Entité livraison
│   ├── ServiceLivraison.java  # Logique métier (gestion des listes)
│   ├── MenuCLI.java           # Interface terminal (JLine, couleurs, navigation)
│   └── Main.java              # Point d'entrée, menus et interactions
├── pom.xml                    # Dépendances Maven
├── run.sh                     # Script de lancement
└── README.md
```

---

## Dépendances

| Bibliothèque | Version | Usage |
|---|---|---|
| [JLine](https://github.com/jline/jline3) | 3.25.1 | Navigation flèches, raw mode terminal, lecture clavier |

---

## Troubleshooting

**Les flèches ne marchent pas :**
→ Tu es probablement dans la console Run d'IntelliJ. Lance depuis l'onglet Terminal ou un terminal externe.

**Warning "restricted method" :**
→ Ajouter `--enable-native-access=ALL-UNNAMED` dans les VM options.

**Warning "dumb terminal" :**
→ Normal dans la console Run d'IntelliJ. Utilise un vrai terminal.

**`ClassNotFoundException: org.jline...` :**
→ Le jar JLine n'est pas dans le classpath. Vérifier le `-cp` dans la commande ou la config Maven.

**Les emojis ne s'affichent pas :**
→ Utiliser un terminal compatible UTF-8 (Terminal.app, iTerm2, Windows Terminal).