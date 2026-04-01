# Systeme de Livraison - Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a fully functional Java CLI application for managing clients, livreurs, commandes, and livraisons with interactive arrow-key menus (JLine), real CRUD operations, and advanced features (tri, stats, types de livraison).

**Architecture:** 6 entity/enum classes (Client, Livreur, Commande, Livraison, StatutCommande, TypeLivraison), 1 service class (ServiceLivraison) holding ArrayLists and all business logic, 1 UI utility class (MenuCLI) encapsulating JLine terminal interaction (menus, text input, table rendering), and Main orchestrating menus. JLine is already configured for arrow-key navigation; text input uses JLine's LineReader.

**Tech Stack:** Java 21+, JLine 3.25.1 (terminal interaction), no build tool (IntelliJ compile to out/)

---

## Current State Analysis

**What exists and works:**
- `Client.java` - has attributes (id, nom, prenom, telephone, email, adresse) with auto-increment id, getters/setters, `afficherDetails()`. Needs: search not in class (will be in ServiceLivraison), `getSurname()` should be `getPrenom()`.
- `Livreur.java` - has id, nom, prenom, telephone, but uses `voiture`+`plaque` instead of `vehicule` per spec. `afficherDetails()` prints "Client" instead of "Livreur". Needs full rework of attributes.
- `Commande.java` - empty class. Needs full implementation.
- `Livraison.java` - empty class. Needs full implementation.
- `ServiceLivraison.java` - empty class. Needs full implementation.
- `Main.java` - has working JLine arrow-key menu navigation (`selectionner()`), color constants, `clearScreen()`, `pause()`. But all menu actions are hardcoded demo data, no real CRUD.
- `run.sh` - hardcoded path, functional.

**What needs to be built:**
1. Fix `Client.java` (rename getSurname -> getPrenom)
2. Rewrite `Livreur.java` (vehicule instead of voiture/plaque, fix afficherDetails)
3. Create `StatutCommande.java` enum (EN_ATTENTE, EN_PREPARATION, EN_LIVRAISON, LIVREE)
4. Create `TypeLivraison.java` enum (STANDARD, EXPRESS) - advanced feature
5. Implement `Commande.java` (id, client, description, dateCommande, statut, methods)
6. Implement `Livraison.java` (commande, livreur, dates, statut, type, methods)
7. Implement `ServiceLivraison.java` (all CRUD + search + stats)
8. Create `MenuCLI.java` (extract UI utilities from Main + add text input, table rendering)
9. Rewrite `Main.java` (real interactive menus calling ServiceLivraison)

**Advanced features to implement (spec requires at least 2, we'll do 5):**
- Tri des clients par nom
- Tri des commandes par date
- Statistiques livreurs les plus actifs
- Commandes d'un client donne
- Types de livraison (express/standard)

## File Structure

```
src/
  Client.java           -- MODIFY: rename getSurname->getPrenom
  Livreur.java          -- REWRITE: vehicule, fix afficherDetails
  StatutCommande.java   -- CREATE: enum (EN_ATTENTE, EN_PREPARATION, EN_LIVRAISON, LIVREE)
  TypeLivraison.java    -- CREATE: enum (STANDARD, EXPRESS)
  Commande.java         -- REWRITE: full implementation
  Livraison.java        -- REWRITE: full implementation
  ServiceLivraison.java -- REWRITE: full business logic
  MenuCLI.java          -- CREATE: UI utilities extracted from Main + text input
  Main.java             -- REWRITE: real menus with CRUD operations
```

---

### Task 1: Fix Client.java

**Files:**
- Modify: `src/Client.java`

- [ ] **Step 1: Rename getSurname to getPrenom and add setNom/setPrenom**

Replace the current `Client.java` with this corrected version:

```java
public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private static int compteur = 1;

    public Client(String nom, String prenom, String telephone, String email, String adresse) {
        this.id = compteur++;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    public void afficherDetails() {
        System.out.println("  +-----------------------------+");
        System.out.printf("  | Client #%-19d |%n", id);
        System.out.printf("  | Nom:       %-17s |%n", nom + " " + prenom);
        System.out.printf("  | Telephone: %-17s |%n", telephone);
        System.out.printf("  | Email:     %-17s |%n", email);
        System.out.printf("  | Adresse:   %-17s |%n", adresse);
        System.out.println("  +-----------------------------+");
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @Override
    public String toString() {
        return "#" + id + " " + nom + " " + prenom;
    }
}
```

Key changes from current:
- `compteur` starts at 1 (not 0)
- Renamed `getSurname()` to `getPrenom()`
- Added `setNom()`, `setPrenom()`
- Removed Unicode box-drawing in `afficherDetails()` (replaced with ASCII `+---+` for broader terminal compat)
- `toString()` returns concise format for use in selection menus

- [ ] **Step 2: Build and verify**

Run: `Cmd+F9` in IntelliJ or compile from terminal
Expected: No compilation errors

- [ ] **Step 3: Commit**

```bash
git add src/Client.java
git commit -m "fix: Client - rename getSurname to getPrenom, start ID at 1"
```

---

### Task 2: Rewrite Livreur.java

**Files:**
- Modify: `src/Livreur.java`

- [ ] **Step 1: Rewrite Livreur with vehicule attribute per spec**

Replace the current `Livreur.java`:

```java
public class Livreur {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;
    private static int compteur = 1;

    public Livreur(String nom, String prenom, String telephone, String vehicule) {
        this.id = compteur++;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.vehicule = vehicule;
    }

    public void afficherDetails() {
        System.out.println("  +-----------------------------+");
        System.out.printf("  | Livreur #%-18d |%n", id);
        System.out.printf("  | Nom:       %-17s |%n", nom + " " + prenom);
        System.out.printf("  | Telephone: %-17s |%n", telephone);
        System.out.printf("  | Vehicule:  %-17s |%n", vehicule);
        System.out.println("  +-----------------------------+");
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }

    @Override
    public String toString() {
        return "#" + id + " " + nom + " " + prenom + " (" + vehicule + ")";
    }
}
```

Key changes:
- Removed `voiture` + `plaque`, replaced with single `vehicule` per spec
- Auto-increment ID (was manual int param before)
- Fixed `afficherDetails()` - was printing "Client" instead of "Livreur"
- Added `setNom()`, `setPrenom()`

- [ ] **Step 2: Build and verify no compilation errors**

- [ ] **Step 3: Commit**

```bash
git add src/Livreur.java
git commit -m "fix: Livreur - use vehicule per spec, auto-increment ID, fix display"
```

---

### Task 3: Create StatutCommande enum

**Files:**
- Create: `src/StatutCommande.java`

- [ ] **Step 1: Create the enum**

```java
public enum StatutCommande {
    EN_ATTENTE("En attente"),
    EN_PREPARATION("En preparation"),
    EN_LIVRAISON("En livraison"),
    LIVREE("Livree");

    private final String label;

    StatutCommande(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
```

- [ ] **Step 2: Build and verify**

- [ ] **Step 3: Commit**

```bash
git add src/StatutCommande.java
git commit -m "feat: add StatutCommande enum"
```

---

### Task 4: Create TypeLivraison enum (advanced feature)

**Files:**
- Create: `src/TypeLivraison.java`

- [ ] **Step 1: Create the enum**

```java
public enum TypeLivraison {
    STANDARD("Standard"),
    EXPRESS("Express");

    private final String label;

    TypeLivraison(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
```

- [ ] **Step 2: Build and verify**

- [ ] **Step 3: Commit**

```bash
git add src/TypeLivraison.java
git commit -m "feat: add TypeLivraison enum (express/standard)"
```

---

### Task 5: Implement Commande.java

**Files:**
- Modify: `src/Commande.java`

- [ ] **Step 1: Write the full Commande class**

```java
import java.time.LocalDate;

public class Commande {
    private int id;
    private Client client;
    private String description;
    private LocalDate dateCommande;
    private StatutCommande statut;
    private static int compteur = 1;

    public Commande(Client client, String description) {
        this.id = compteur++;
        this.client = client;
        this.description = description;
        this.dateCommande = LocalDate.now();
        this.statut = StatutCommande.EN_ATTENTE;
    }

    public void afficherCommande() {
        System.out.println("  +--------------------------------------+");
        System.out.printf("  | Commande #%-27d |%n", id);
        System.out.printf("  | Client:      %-24s |%n", client.getNom() + " " + client.getPrenom());
        System.out.printf("  | Description: %-24s |%n", description);
        System.out.printf("  | Date:        %-24s |%n", dateCommande);
        System.out.printf("  | Statut:      %-24s |%n", statut.getLabel());
        System.out.println("  +--------------------------------------+");
    }

    public void modifierStatut(StatutCommande nouveauStatut) {
        this.statut = nouveauStatut;
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateCommande() { return dateCommande; }
    public StatutCommande getStatut() { return statut; }

    @Override
    public String toString() {
        return "#" + id + " - " + description + " (" + statut.getLabel() + ")";
    }
}
```

- [ ] **Step 2: Build and verify**

- [ ] **Step 3: Commit**

```bash
git add src/Commande.java
git commit -m "feat: implement Commande with client, description, date, statut"
```

---

### Task 6: Implement Livraison.java

**Files:**
- Modify: `src/Livraison.java`

- [ ] **Step 1: Write the full Livraison class**

```java
import java.time.LocalDate;

public class Livraison {
    private int id;
    private Commande commande;
    private Livreur livreur;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    private TypeLivraison type;
    private static int compteur = 1;

    public Livraison(Commande commande, Livreur livreur, LocalDate datePrevue, TypeLivraison type) {
        this.id = compteur++;
        this.commande = commande;
        this.livreur = livreur;
        this.dateLivraisonPrevue = datePrevue;
        this.dateLivraisonReelle = null;
        this.type = type;
        commande.modifierStatut(StatutCommande.EN_LIVRAISON);
    }

    public void afficherLivraison() {
        System.out.println("  +--------------------------------------+");
        System.out.printf("  | Livraison #%-26d |%n", id);
        System.out.printf("  | Commande:    #%-23d |%n", commande.getId());
        System.out.printf("  | Client:      %-24s |%n", commande.getClient().getNom() + " " + commande.getClient().getPrenom());
        System.out.printf("  | Livreur:     %-24s |%n", livreur.getNom() + " " + livreur.getPrenom());
        System.out.printf("  | Type:        %-24s |%n", type.getLabel());
        System.out.printf("  | Prevue:      %-24s |%n", dateLivraisonPrevue);
        String reelleStr = dateLivraisonReelle != null ? dateLivraisonReelle.toString() : "---";
        System.out.printf("  | Livree:      %-24s |%n", reelleStr);
        String statut = dateLivraisonReelle != null ? "Terminee" : "En cours";
        System.out.printf("  | Statut:      %-24s |%n", statut);
        System.out.println("  +--------------------------------------+");
    }

    public void terminerLivraison() {
        this.dateLivraisonReelle = LocalDate.now();
        commande.modifierStatut(StatutCommande.LIVREE);
    }

    public boolean estTerminee() {
        return dateLivraisonReelle != null;
    }

    public int getId() { return id; }
    public Commande getCommande() { return commande; }
    public Livreur getLivreur() { return livreur; }
    public LocalDate getDateLivraisonPrevue() { return dateLivraisonPrevue; }
    public LocalDate getDateLivraisonReelle() { return dateLivraisonReelle; }
    public TypeLivraison getType() { return type; }

    @Override
    public String toString() {
        String statut = estTerminee() ? "Terminee" : "En cours";
        return "#" + id + " Cmd#" + commande.getId() + " -> " + livreur.getNom() + " (" + statut + ")";
    }
}
```

- [ ] **Step 2: Build and verify**

- [ ] **Step 3: Commit**

```bash
git add src/Livraison.java
git commit -m "feat: implement Livraison with commande, livreur, dates, type, terminer"
```

---

### Task 7: Implement ServiceLivraison.java

**Files:**
- Modify: `src/ServiceLivraison.java`

- [ ] **Step 1: Write ServiceLivraison with all CRUD + advanced features**

```java
import java.util.ArrayList;
import java.util.Comparator;

public class ServiceLivraison {
    private ArrayList<Client> listeClients = new ArrayList<>();
    private ArrayList<Livreur> listeLivreurs = new ArrayList<>();
    private ArrayList<Commande> listeCommandes = new ArrayList<>();
    private ArrayList<Livraison> listeLivraisons = new ArrayList<>();

    // ═══ CLIENTS ═══

    public void ajouterClient(Client client) {
        listeClients.add(client);
    }

    public boolean supprimerClient(int id) {
        return listeClients.removeIf(c -> c.getId() == id);
    }

    public Client rechercherClientParId(int id) {
        for (Client c : listeClients) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public ArrayList<Client> rechercherClientParNom(String nom) {
        ArrayList<Client> resultats = new ArrayList<>();
        String nomLower = nom.toLowerCase();
        for (Client c : listeClients) {
            if (c.getNom().toLowerCase().contains(nomLower)
                || c.getPrenom().toLowerCase().contains(nomLower)) {
                resultats.add(c);
            }
        }
        return resultats;
    }

    public ArrayList<Client> getClientsTriesParNom() {
        ArrayList<Client> copie = new ArrayList<>(listeClients);
        copie.sort(Comparator.comparing(Client::getNom)
                .thenComparing(Client::getPrenom));
        return copie;
    }

    public ArrayList<Client> getListeClients() {
        return listeClients;
    }

    // ═══ LIVREURS ═══

    public void ajouterLivreur(Livreur livreur) {
        listeLivreurs.add(livreur);
    }

    public boolean supprimerLivreur(int id) {
        return listeLivreurs.removeIf(l -> l.getId() == id);
    }

    public Livreur rechercherLivreurParId(int id) {
        for (Livreur l : listeLivreurs) {
            if (l.getId() == id) return l;
        }
        return null;
    }

    public ArrayList<Livreur> rechercherLivreurParNom(String nom) {
        ArrayList<Livreur> resultats = new ArrayList<>();
        String nomLower = nom.toLowerCase();
        for (Livreur l : listeLivreurs) {
            if (l.getNom().toLowerCase().contains(nomLower)
                || l.getPrenom().toLowerCase().contains(nomLower)) {
                resultats.add(l);
            }
        }
        return resultats;
    }

    public ArrayList<Livreur> getListeLivreurs() {
        return listeLivreurs;
    }

    // ═══ COMMANDES ═══

    public void creerCommande(Commande commande) {
        listeCommandes.add(commande);
    }

    public boolean supprimerCommande(int id) {
        return listeCommandes.removeIf(c -> c.getId() == id);
    }

    public Commande rechercherCommandeParId(int id) {
        for (Commande c : listeCommandes) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public ArrayList<Commande> getCommandesTrieesParDate() {
        ArrayList<Commande> copie = new ArrayList<>(listeCommandes);
        copie.sort(Comparator.comparing(Commande::getDateCommande).reversed());
        return copie;
    }

    public ArrayList<Commande> getCommandesParClient(Client client) {
        ArrayList<Commande> resultats = new ArrayList<>();
        for (Commande c : listeCommandes) {
            if (c.getClient().getId() == client.getId()) {
                resultats.add(c);
            }
        }
        return resultats;
    }

    public ArrayList<Commande> getCommandesEnLivraison() {
        ArrayList<Commande> resultats = new ArrayList<>();
        for (Commande c : listeCommandes) {
            if (c.getStatut() == StatutCommande.EN_LIVRAISON) {
                resultats.add(c);
            }
        }
        return resultats;
    }

    public ArrayList<Commande> getListeCommandes() {
        return listeCommandes;
    }

    // ═══ LIVRAISONS ═══

    public void affecterLivraison(Livraison livraison) {
        listeLivraisons.add(livraison);
    }

    public ArrayList<Livraison> getLivraisonsEnCours() {
        ArrayList<Livraison> resultats = new ArrayList<>();
        for (Livraison l : listeLivraisons) {
            if (!l.estTerminee()) {
                resultats.add(l);
            }
        }
        return resultats;
    }

    public ArrayList<Livraison> getLivraisonsTerminees() {
        ArrayList<Livraison> resultats = new ArrayList<>();
        for (Livraison l : listeLivraisons) {
            if (l.estTerminee()) {
                resultats.add(l);
            }
        }
        return resultats;
    }

    public ArrayList<Livraison> getListeLivraisons() {
        return listeLivraisons;
    }

    // ═══ STATISTIQUES (fonctionnalites avancees) ═══

    public int getNombreCommandesLivrees() {
        int count = 0;
        for (Commande c : listeCommandes) {
            if (c.getStatut() == StatutCommande.LIVREE) count++;
        }
        return count;
    }

    public int getNombreLivraisonsParLivreur(Livreur livreur) {
        int count = 0;
        for (Livraison l : listeLivraisons) {
            if (l.getLivreur().getId() == livreur.getId() && l.estTerminee()) {
                count++;
            }
        }
        return count;
    }

    public Livreur getLivreurPlusActif() {
        Livreur meilleur = null;
        int max = 0;
        for (Livreur l : listeLivreurs) {
            int nb = getNombreLivraisonsParLivreur(l);
            if (nb > max) {
                max = nb;
                meilleur = l;
            }
        }
        return meilleur;
    }
}
```

- [ ] **Step 2: Build and verify**

- [ ] **Step 3: Commit**

```bash
git add src/ServiceLivraison.java
git commit -m "feat: implement ServiceLivraison with full CRUD, search, tri, stats"
```

---

### Task 8: Create MenuCLI.java (UI utility class)

**Files:**
- Create: `src/MenuCLI.java`

This class encapsulates all terminal interaction: JLine setup, arrow-key menu selection, text/number input, table display, colors. Main.java will use this instead of having UI code inline.

- [ ] **Step 1: Write MenuCLI**

```java
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;
import java.io.IOException;

public class MenuCLI {
    // Couleurs
    public static final String RESET = "\u001B[0m";
    public static final String BOLD  = "\u001B[1m";
    public static final String DIM   = "\u001B[2m";
    public static final String VERT  = "\u001B[32m";
    public static final String ROUGE = "\u001B[31m";
    public static final String JAUNE = "\u001B[33m";
    public static final String CYAN  = "\u001B[36m";

    private Terminal terminal;
    private NonBlockingReader reader;

    public MenuCLI() throws IOException {
        terminal = TerminalBuilder.builder()
                .system(true)
                .jansi(false)
                .build();
    }

    public void close() throws IOException {
        terminal.close();
    }

    // ═══ MENU SELECTION (fleches) ═══

    public int selectionner(String titre, String[] options) throws IOException {
        terminal.enterRawMode();
        int selection = 0;

        while (true) {
            clearScreen();
            System.out.println();
            System.out.println(CYAN + BOLD + "  +==========================================+");
            System.out.printf( "  |  %-40s |%n", titre);
            System.out.println("  +==========================================+" + RESET);
            System.out.println();

            for (int i = 0; i < options.length; i++) {
                if (i == selection) {
                    System.out.println(CYAN + BOLD + "   > " + options[i] + RESET);
                } else {
                    System.out.println(DIM +       "     " + options[i] + RESET);
                }
            }

            System.out.println();
            System.out.println(DIM + "  [^v] Naviguer  |  [Entree] Valider" + RESET);

            int key = reader().read();
            if (key == 27) {
                int next = reader().read();
                if (next == '[') {
                    int arrow = reader().read();
                    if (arrow == 'A') selection = (selection - 1 + options.length) % options.length;
                    if (arrow == 'B') selection = (selection + 1) % options.length;
                }
            } else if (key == 13 || key == 10) {
                return selection;
            }
        }
    }

    // ═══ TEXT INPUT ═══

    public String lireTexte(String prompt) throws IOException {
        // Exit raw mode for line input
        System.out.print(CYAN + "  " + prompt + ": " + RESET);
        System.out.flush();

        StringBuilder sb = new StringBuilder();
        while (true) {
            int c = reader().read();
            if (c == 13 || c == 10) {
                System.out.println();
                return sb.toString().trim();
            } else if (c == 127 || c == 8) { // backspace
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    System.out.print("\b \b");
                    System.out.flush();
                }
            } else if (c == 27) {
                // consume escape sequences (arrow keys during text input)
                reader().read();
                reader().read();
            } else if (c >= 32) {
                sb.append((char) c);
                System.out.print((char) c);
                System.out.flush();
            }
        }
    }

    public int lireEntier(String prompt) throws IOException {
        while (true) {
            String texte = lireTexte(prompt);
            try {
                return Integer.parseInt(texte);
            } catch (NumberFormatException e) {
                System.out.println(ROUGE + "  Entrez un nombre valide." + RESET);
            }
        }
    }

    // ═══ DISPLAY HELPERS ═══

    public void afficherSucces(String message) {
        System.out.println(VERT + "\n  " + message + RESET);
    }

    public void afficherErreur(String message) {
        System.out.println(ROUGE + "\n  " + message + RESET);
    }

    public void afficherTitre(String titre) {
        System.out.println(JAUNE + BOLD + "\n  -- " + titre + " --\n" + RESET);
    }

    public void afficherInfo(String message) {
        System.out.println(DIM + "  " + message + RESET);
    }

    public void pause() throws IOException {
        System.out.println(DIM + "\n  Appuyez sur une touche..." + RESET);
        reader().read();
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private NonBlockingReader reader() {
        if (reader == null) {
            reader = terminal.reader();
        }
        return reader;
    }
}
```

- [ ] **Step 2: Build and verify**

- [ ] **Step 3: Commit**

```bash
git add src/MenuCLI.java
git commit -m "feat: create MenuCLI - JLine UI utilities (menus, input, display)"
```

---

### Task 9: Rewrite Main.java - Core structure + Client menus

**Files:**
- Modify: `src/Main.java`

- [ ] **Step 1: Write Main with menu structure and Client CRUD**

Replace the entire `Main.java`:

```java
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Main {

    static MenuCLI cli;
    static ServiceLivraison service = new ServiceLivraison();

    public static void main(String[] args) throws Exception {
        cli = new MenuCLI();

        // Donnees de demo
        chargerDonneesDemoInit();

        boolean running = true;
        while (running) {
            int choix = cli.selectionner("SYSTEME DE LIVRAISON", new String[]{
                    "Gestion des clients",
                    "Gestion des livreurs",
                    "Gestion des commandes",
                    "Gestion des livraisons",
                    "Statistiques",
                    "Quitter"
            });

            switch (choix) {
                case 0 -> menuClients();
                case 1 -> menuLivreurs();
                case 2 -> menuCommandes();
                case 3 -> menuLivraisons();
                case 4 -> menuStatistiques();
                case 5 -> running = false;
            }
        }

        cli.clearScreen();
        System.out.println(MenuCLI.VERT + MenuCLI.BOLD + "\n  A bientot !\n" + MenuCLI.RESET);
        cli.close();
    }

    // ═══════════════════════════════════════
    //             DONNEES DE DEMO
    // ═══════════════════════════════════════

    static void chargerDonneesDemoInit() {
        service.ajouterClient(new Client("Dupont", "Jean", "06.12.34.56.78", "jean@mail.com", "12 rue de Lyon"));
        service.ajouterClient(new Client("Martin", "Claire", "07.65.43.21.00", "claire@mail.com", "5 av Bellecour"));
        service.ajouterClient(new Client("Bernard", "Lucas", "06.98.76.54.32", "lucas@mail.com", "8 rue Garibaldi"));

        service.ajouterLivreur(new Livreur("Moreau", "Pierre", "06.11.22.33.44", "Scooter"));
        service.ajouterLivreur(new Livreur("Leroy", "Sophie", "07.55.66.77.88", "Velo cargo"));

        Commande c1 = new Commande(service.getListeClients().get(0), "Colis electronique");
        Commande c2 = new Commande(service.getListeClients().get(1), "Paquet vetements");
        Commande c3 = new Commande(service.getListeClients().get(0), "Livre informatique");
        service.creerCommande(c1);
        service.creerCommande(c2);
        service.creerCommande(c3);
    }

    // ═══════════════════════════════════════
    //            MENU CLIENTS
    // ═══════════════════════════════════════

    static void menuClients() throws IOException {
        boolean back = false;
        while (!back) {
            int choix = cli.selectionner("GESTION DES CLIENTS", new String[]{
                    "Ajouter un client",
                    "Supprimer un client",
                    "Modifier un client",
                    "Rechercher un client",
                    "Afficher tous les clients",
                    "Afficher clients tries par nom",
                    "Retour"
            });

            switch (choix) {
                case 0 -> ajouterClient();
                case 1 -> supprimerClient();
                case 2 -> modifierClient();
                case 3 -> rechercherClient();
                case 4 -> afficherClients(service.getListeClients(), "Liste des Clients");
                case 5 -> afficherClients(service.getClientsTriesParNom(), "Clients tries par nom");
                case 6 -> back = true;
            }
        }
    }

    static void ajouterClient() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Nouveau Client");
        String nom = cli.lireTexte("Nom");
        String prenom = cli.lireTexte("Prenom");
        String tel = cli.lireTexte("Telephone");
        String email = cli.lireTexte("Email");
        String adresse = cli.lireTexte("Adresse");

        if (nom.isEmpty() || prenom.isEmpty()) {
            cli.afficherErreur("Nom et prenom obligatoires.");
            cli.pause();
            return;
        }

        Client client = new Client(nom, prenom, tel, email, adresse);
        service.ajouterClient(client);
        cli.afficherSucces("Client #" + client.getId() + " cree : " + nom + " " + prenom);
        cli.pause();
    }

    static void supprimerClient() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Supprimer un Client");
        if (service.getListeClients().isEmpty()) {
            cli.afficherErreur("Aucun client enregistre.");
            cli.pause();
            return;
        }

        int id = cli.lireEntier("ID du client a supprimer");
        Client client = service.rechercherClientParId(id);
        if (client == null) {
            cli.afficherErreur("Client #" + id + " introuvable.");
        } else {
            service.supprimerClient(id);
            cli.afficherSucces("Client #" + id + " supprime.");
        }
        cli.pause();
    }

    static void modifierClient() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Modifier un Client");
        int id = cli.lireEntier("ID du client a modifier");
        Client client = service.rechercherClientParId(id);
        if (client == null) {
            cli.afficherErreur("Client #" + id + " introuvable.");
            cli.pause();
            return;
        }

        client.afficherDetails();
        System.out.println(MenuCLI.DIM + "  (laisser vide pour ne pas modifier)\n" + MenuCLI.RESET);

        String nom = cli.lireTexte("Nom [" + client.getNom() + "]");
        if (!nom.isEmpty()) client.setNom(nom);
        String prenom = cli.lireTexte("Prenom [" + client.getPrenom() + "]");
        if (!prenom.isEmpty()) client.setPrenom(prenom);
        String tel = cli.lireTexte("Telephone [" + client.getTelephone() + "]");
        if (!tel.isEmpty()) client.setTelephone(tel);
        String email = cli.lireTexte("Email [" + client.getEmail() + "]");
        if (!email.isEmpty()) client.setEmail(email);
        String adresse = cli.lireTexte("Adresse [" + client.getAdresse() + "]");
        if (!adresse.isEmpty()) client.setAdresse(adresse);

        cli.afficherSucces("Client #" + id + " modifie.");
        cli.pause();
    }

    static void rechercherClient() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Rechercher un Client");

        int choix = cli.selectionner("Rechercher par", new String[]{
                "Identifiant",
                "Nom",
                "Retour"
        });

        if (choix == 2) return;

        cli.clearScreen();
        if (choix == 0) {
            int id = cli.lireEntier("ID du client");
            Client client = service.rechercherClientParId(id);
            if (client == null) {
                cli.afficherErreur("Client #" + id + " introuvable.");
            } else {
                System.out.println();
                client.afficherDetails();
            }
        } else {
            String nom = cli.lireTexte("Nom a rechercher");
            ArrayList<Client> resultats = service.rechercherClientParNom(nom);
            if (resultats.isEmpty()) {
                cli.afficherErreur("Aucun client trouve pour \"" + nom + "\".");
            } else {
                afficherClients(resultats, "Resultats (" + resultats.size() + ")");
            }
        }
        cli.pause();
    }

    static void afficherClients(ArrayList<Client> clients, String titre) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
        if (clients.isEmpty()) {
            cli.afficherInfo("Aucun client.");
        } else {
            System.out.printf(MenuCLI.CYAN + "  %-5s %-15s %-15s %-15s %-20s%n" + MenuCLI.RESET,
                    "ID", "Nom", "Prenom", "Telephone", "Email");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(70) + MenuCLI.RESET);
            for (Client c : clients) {
                System.out.printf("  %-5d %-15s %-15s %-15s %-20s%n",
                        c.getId(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getEmail());
            }
            System.out.println(MenuCLI.DIM + "\n  Total : " + clients.size() + " client(s)" + MenuCLI.RESET);
        }
        cli.pause();
    }

    // ═══════════════════════════════════════
    //            MENU LIVREURS
    // ═══════════════════════════════════════

    static void menuLivreurs() throws IOException {
        boolean back = false;
        while (!back) {
            int choix = cli.selectionner("GESTION DES LIVREURS", new String[]{
                    "Ajouter un livreur",
                    "Supprimer un livreur",
                    "Modifier un livreur",
                    "Rechercher un livreur",
                    "Afficher tous les livreurs",
                    "Retour"
            });

            switch (choix) {
                case 0 -> ajouterLivreur();
                case 1 -> supprimerLivreur();
                case 2 -> modifierLivreur();
                case 3 -> rechercherLivreur();
                case 4 -> afficherLivreurs();
                case 5 -> back = true;
            }
        }
    }

    static void ajouterLivreur() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Nouveau Livreur");
        String nom = cli.lireTexte("Nom");
        String prenom = cli.lireTexte("Prenom");
        String tel = cli.lireTexte("Telephone");
        String vehicule = cli.lireTexte("Vehicule");

        if (nom.isEmpty() || prenom.isEmpty()) {
            cli.afficherErreur("Nom et prenom obligatoires.");
            cli.pause();
            return;
        }

        Livreur livreur = new Livreur(nom, prenom, tel, vehicule);
        service.ajouterLivreur(livreur);
        cli.afficherSucces("Livreur #" + livreur.getId() + " cree : " + nom + " " + prenom);
        cli.pause();
    }

    static void supprimerLivreur() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Supprimer un Livreur");
        if (service.getListeLivreurs().isEmpty()) {
            cli.afficherErreur("Aucun livreur enregistre.");
            cli.pause();
            return;
        }

        int id = cli.lireEntier("ID du livreur a supprimer");
        Livreur livreur = service.rechercherLivreurParId(id);
        if (livreur == null) {
            cli.afficherErreur("Livreur #" + id + " introuvable.");
        } else {
            service.supprimerLivreur(id);
            cli.afficherSucces("Livreur #" + id + " supprime.");
        }
        cli.pause();
    }

    static void modifierLivreur() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Modifier un Livreur");
        int id = cli.lireEntier("ID du livreur a modifier");
        Livreur livreur = service.rechercherLivreurParId(id);
        if (livreur == null) {
            cli.afficherErreur("Livreur #" + id + " introuvable.");
            cli.pause();
            return;
        }

        livreur.afficherDetails();
        System.out.println(MenuCLI.DIM + "  (laisser vide pour ne pas modifier)\n" + MenuCLI.RESET);

        String nom = cli.lireTexte("Nom [" + livreur.getNom() + "]");
        if (!nom.isEmpty()) livreur.setNom(nom);
        String prenom = cli.lireTexte("Prenom [" + livreur.getPrenom() + "]");
        if (!prenom.isEmpty()) livreur.setPrenom(prenom);
        String tel = cli.lireTexte("Telephone [" + livreur.getTelephone() + "]");
        if (!tel.isEmpty()) livreur.setTelephone(tel);
        String vehicule = cli.lireTexte("Vehicule [" + livreur.getVehicule() + "]");
        if (!vehicule.isEmpty()) livreur.setVehicule(vehicule);

        cli.afficherSucces("Livreur #" + id + " modifie.");
        cli.pause();
    }

    static void rechercherLivreur() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Rechercher un Livreur");

        int choix = cli.selectionner("Rechercher par", new String[]{
                "Identifiant",
                "Nom",
                "Retour"
        });

        if (choix == 2) return;

        cli.clearScreen();
        if (choix == 0) {
            int id = cli.lireEntier("ID du livreur");
            Livreur livreur = service.rechercherLivreurParId(id);
            if (livreur == null) {
                cli.afficherErreur("Livreur #" + id + " introuvable.");
            } else {
                System.out.println();
                livreur.afficherDetails();
            }
        } else {
            String nom = cli.lireTexte("Nom a rechercher");
            ArrayList<Livreur> resultats = service.rechercherLivreurParNom(nom);
            if (resultats.isEmpty()) {
                cli.afficherErreur("Aucun livreur trouve pour \"" + nom + "\".");
            } else {
                System.out.printf(MenuCLI.CYAN + "\n  %-5s %-15s %-15s %-15s %-15s%n" + MenuCLI.RESET,
                        "ID", "Nom", "Prenom", "Telephone", "Vehicule");
                System.out.println(MenuCLI.DIM + "  " + "-".repeat(65) + MenuCLI.RESET);
                for (Livreur l : resultats) {
                    System.out.printf("  %-5d %-15s %-15s %-15s %-15s%n",
                            l.getId(), l.getNom(), l.getPrenom(), l.getTelephone(), l.getVehicule());
                }
            }
        }
        cli.pause();
    }

    static void afficherLivreurs() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Liste des Livreurs");
        ArrayList<Livreur> livreurs = service.getListeLivreurs();
        if (livreurs.isEmpty()) {
            cli.afficherInfo("Aucun livreur.");
        } else {
            System.out.printf(MenuCLI.CYAN + "  %-5s %-15s %-15s %-15s %-15s%n" + MenuCLI.RESET,
                    "ID", "Nom", "Prenom", "Telephone", "Vehicule");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(65) + MenuCLI.RESET);
            for (Livreur l : livreurs) {
                System.out.printf("  %-5d %-15s %-15s %-15s %-15s%n",
                        l.getId(), l.getNom(), l.getPrenom(), l.getTelephone(), l.getVehicule());
            }
            System.out.println(MenuCLI.DIM + "\n  Total : " + livreurs.size() + " livreur(s)" + MenuCLI.RESET);
        }
        cli.pause();
    }

    // ═══════════════════════════════════════
    //           MENU COMMANDES
    // ═══════════════════════════════════════

    static void menuCommandes() throws IOException {
        boolean back = false;
        while (!back) {
            int choix = cli.selectionner("GESTION DES COMMANDES", new String[]{
                    "Creer une commande",
                    "Supprimer une commande",
                    "Rechercher une commande",
                    "Afficher toutes les commandes",
                    "Commandes triees par date",
                    "Commandes d'un client",
                    "Commandes en cours de livraison",
                    "Retour"
            });

            switch (choix) {
                case 0 -> creerCommande();
                case 1 -> supprimerCommande();
                case 2 -> rechercherCommande();
                case 3 -> afficherCommandes(service.getListeCommandes(), "Toutes les Commandes");
                case 4 -> afficherCommandes(service.getCommandesTrieesParDate(), "Commandes triees par date");
                case 5 -> commandesParClient();
                case 6 -> afficherCommandes(service.getCommandesEnLivraison(), "Commandes en livraison");
                case 7 -> back = true;
            }
        }
    }

    static void creerCommande() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Nouvelle Commande");

        if (service.getListeClients().isEmpty()) {
            cli.afficherErreur("Aucun client. Ajoutez un client d'abord.");
            cli.pause();
            return;
        }

        // Selection du client par menu
        ArrayList<Client> clients = service.getListeClients();
        String[] optionsClients = new String[clients.size() + 1];
        for (int i = 0; i < clients.size(); i++) {
            optionsClients[i] = clients.get(i).toString();
        }
        optionsClients[clients.size()] = "Annuler";

        int choixClient = cli.selectionner("Choisir le client", optionsClients);
        if (choixClient == clients.size()) return;

        cli.clearScreen();
        cli.afficherTitre("Nouvelle Commande pour " + clients.get(choixClient).getNom());
        String description = cli.lireTexte("Description");

        if (description.isEmpty()) {
            cli.afficherErreur("Description obligatoire.");
            cli.pause();
            return;
        }

        Commande commande = new Commande(clients.get(choixClient), description);
        service.creerCommande(commande);
        cli.afficherSucces("Commande #" + commande.getId() + " creee.");
        cli.pause();
    }

    static void supprimerCommande() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Supprimer une Commande");
        int id = cli.lireEntier("ID de la commande");
        Commande commande = service.rechercherCommandeParId(id);
        if (commande == null) {
            cli.afficherErreur("Commande #" + id + " introuvable.");
        } else {
            service.supprimerCommande(id);
            cli.afficherSucces("Commande #" + id + " supprimee.");
        }
        cli.pause();
    }

    static void rechercherCommande() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Rechercher une Commande");
        int id = cli.lireEntier("ID de la commande");
        Commande commande = service.rechercherCommandeParId(id);
        if (commande == null) {
            cli.afficherErreur("Commande #" + id + " introuvable.");
        } else {
            System.out.println();
            commande.afficherCommande();
        }
        cli.pause();
    }

    static void commandesParClient() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Commandes d'un Client");

        ArrayList<Client> clients = service.getListeClients();
        if (clients.isEmpty()) {
            cli.afficherErreur("Aucun client.");
            cli.pause();
            return;
        }

        String[] optionsClients = new String[clients.size() + 1];
        for (int i = 0; i < clients.size(); i++) {
            optionsClients[i] = clients.get(i).toString();
        }
        optionsClients[clients.size()] = "Annuler";

        int choix = cli.selectionner("Choisir le client", optionsClients);
        if (choix == clients.size()) return;

        Client client = clients.get(choix);
        ArrayList<Commande> commandes = service.getCommandesParClient(client);
        afficherCommandes(commandes, "Commandes de " + client.getNom() + " " + client.getPrenom());
    }

    static void afficherCommandes(ArrayList<Commande> commandes, String titre) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
        if (commandes.isEmpty()) {
            cli.afficherInfo("Aucune commande.");
        } else {
            System.out.printf(MenuCLI.CYAN + "  %-5s %-20s %-20s %-12s %-15s%n" + MenuCLI.RESET,
                    "ID", "Client", "Description", "Date", "Statut");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(72) + MenuCLI.RESET);
            for (Commande c : commandes) {
                System.out.printf("  %-5d %-20s %-20s %-12s %-15s%n",
                        c.getId(),
                        c.getClient().getNom() + " " + c.getClient().getPrenom(),
                        c.getDescription(),
                        c.getDateCommande(),
                        c.getStatut().getLabel());
            }
            System.out.println(MenuCLI.DIM + "\n  Total : " + commandes.size() + " commande(s)" + MenuCLI.RESET);
        }
        cli.pause();
    }

    // ═══════════════════════════════════════
    //           MENU LIVRAISONS
    // ═══════════════════════════════════════

    static void menuLivraisons() throws IOException {
        boolean back = false;
        while (!back) {
            int choix = cli.selectionner("GESTION DES LIVRAISONS", new String[]{
                    "Affecter une livraison",
                    "Terminer une livraison",
                    "Livraisons en cours",
                    "Historique (livraisons terminees)",
                    "Retour"
            });

            switch (choix) {
                case 0 -> affecterLivraison();
                case 1 -> terminerLivraison();
                case 2 -> afficherLivraisons(service.getLivraisonsEnCours(), "Livraisons en cours");
                case 3 -> afficherLivraisons(service.getLivraisonsTerminees(), "Historique des livraisons");
                case 4 -> back = true;
            }
        }
    }

    static void affecterLivraison() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Affecter une Livraison");

        // Filtrer commandes affectables (EN_ATTENTE ou EN_PREPARATION)
        ArrayList<Commande> affectables = new ArrayList<>();
        for (Commande c : service.getListeCommandes()) {
            if (c.getStatut() == StatutCommande.EN_ATTENTE || c.getStatut() == StatutCommande.EN_PREPARATION) {
                affectables.add(c);
            }
        }

        if (affectables.isEmpty()) {
            cli.afficherErreur("Aucune commande disponible pour livraison.");
            cli.pause();
            return;
        }

        if (service.getListeLivreurs().isEmpty()) {
            cli.afficherErreur("Aucun livreur disponible.");
            cli.pause();
            return;
        }

        // Choisir la commande
        String[] optionsCmd = new String[affectables.size() + 1];
        for (int i = 0; i < affectables.size(); i++) {
            optionsCmd[i] = affectables.get(i).toString();
        }
        optionsCmd[affectables.size()] = "Annuler";

        int choixCmd = cli.selectionner("Choisir la commande", optionsCmd);
        if (choixCmd == affectables.size()) return;

        // Choisir le livreur
        ArrayList<Livreur> livreurs = service.getListeLivreurs();
        String[] optionsLiv = new String[livreurs.size() + 1];
        for (int i = 0; i < livreurs.size(); i++) {
            optionsLiv[i] = livreurs.get(i).toString();
        }
        optionsLiv[livreurs.size()] = "Annuler";

        int choixLiv = cli.selectionner("Choisir le livreur", optionsLiv);
        if (choixLiv == livreurs.size()) return;

        // Choisir le type
        int choixType = cli.selectionner("Type de livraison", new String[]{
                "Standard",
                "Express"
        });
        TypeLivraison type = (choixType == 0) ? TypeLivraison.STANDARD : TypeLivraison.EXPRESS;

        // Date prevue
        cli.clearScreen();
        cli.afficherTitre("Date de livraison prevue");
        cli.afficherInfo("Format: AAAA-MM-JJ (ex: 2026-04-15)");
        cli.afficherInfo("Laisser vide = demain");
        String dateStr = cli.lireTexte("Date prevue");

        LocalDate datePrevue;
        if (dateStr.isEmpty()) {
            datePrevue = LocalDate.now().plusDays(1);
        } else {
            try {
                datePrevue = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                cli.afficherErreur("Format de date invalide. Utilisation de demain.");
                datePrevue = LocalDate.now().plusDays(1);
            }
        }

        Livraison livraison = new Livraison(affectables.get(choixCmd), livreurs.get(choixLiv), datePrevue, type);
        service.affecterLivraison(livraison);
        cli.afficherSucces("Livraison #" + livraison.getId() + " affectee (" + type.getLabel() + ")");
        cli.pause();
    }

    static void terminerLivraison() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Terminer une Livraison");

        ArrayList<Livraison> enCours = service.getLivraisonsEnCours();
        if (enCours.isEmpty()) {
            cli.afficherErreur("Aucune livraison en cours.");
            cli.pause();
            return;
        }

        String[] options = new String[enCours.size() + 1];
        for (int i = 0; i < enCours.size(); i++) {
            options[i] = enCours.get(i).toString();
        }
        options[enCours.size()] = "Annuler";

        int choix = cli.selectionner("Choisir la livraison a terminer", options);
        if (choix == enCours.size()) return;

        enCours.get(choix).terminerLivraison();
        cli.afficherSucces("Livraison terminee !");
        cli.pause();
    }

    static void afficherLivraisons(ArrayList<Livraison> livraisons, String titre) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
        if (livraisons.isEmpty()) {
            cli.afficherInfo("Aucune livraison.");
        } else {
            System.out.printf(MenuCLI.CYAN + "  %-4s %-6s %-15s %-15s %-10s %-12s %-10s%n" + MenuCLI.RESET,
                    "ID", "Cmd", "Client", "Livreur", "Type", "Prevue", "Statut");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(72) + MenuCLI.RESET);
            for (Livraison l : livraisons) {
                String statut = l.estTerminee() ? "Terminee" : "En cours";
                System.out.printf("  %-4d %-6d %-15s %-15s %-10s %-12s %-10s%n",
                        l.getId(),
                        l.getCommande().getId(),
                        l.getCommande().getClient().getNom(),
                        l.getLivreur().getNom(),
                        l.getType().getLabel(),
                        l.getDateLivraisonPrevue(),
                        statut);
            }
            System.out.println(MenuCLI.DIM + "\n  Total : " + livraisons.size() + " livraison(s)" + MenuCLI.RESET);
        }
        cli.pause();
    }

    // ═══════════════════════════════════════
    //           STATISTIQUES
    // ═══════════════════════════════════════

    static void menuStatistiques() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Statistiques");

        System.out.println("  Clients          :  " + MenuCLI.CYAN + service.getListeClients().size() + MenuCLI.RESET);
        System.out.println("  Livreurs         :  " + MenuCLI.CYAN + service.getListeLivreurs().size() + MenuCLI.RESET);
        System.out.println("  Commandes        :  " + MenuCLI.CYAN + service.getListeCommandes().size() + MenuCLI.RESET);
        System.out.println("  Livraisons       :  " + MenuCLI.CYAN + service.getListeLivraisons().size() + MenuCLI.RESET);
        System.out.println("  Livrees          :  " + MenuCLI.VERT + service.getNombreCommandesLivrees() + MenuCLI.RESET);
        System.out.println("  En cours         :  " + MenuCLI.JAUNE + service.getLivraisonsEnCours().size() + MenuCLI.RESET);
        System.out.println();

        // Livreur le plus actif
        Livreur meilleur = service.getLivreurPlusActif();
        if (meilleur != null) {
            int nbLivraisons = service.getNombreLivraisonsParLivreur(meilleur);
            System.out.println("  Livreur + actif  :  " + MenuCLI.BOLD + meilleur.getNom() + " " + meilleur.getPrenom()
                    + " (" + nbLivraisons + " livraison(s))" + MenuCLI.RESET);
        } else {
            System.out.println(MenuCLI.DIM + "  Aucun livreur actif." + MenuCLI.RESET);
        }

        // Stats par livreur
        if (!service.getListeLivreurs().isEmpty()) {
            System.out.println(MenuCLI.JAUNE + "\n  -- Activite par livreur --\n" + MenuCLI.RESET);
            for (Livreur l : service.getListeLivreurs()) {
                int nb = service.getNombreLivraisonsParLivreur(l);
                System.out.printf("  %-25s  %d livraison(s)%n", l.getNom() + " " + l.getPrenom(), nb);
            }
        }

        cli.pause();
    }
}
```

- [ ] **Step 2: Build the project**

Run: `Cmd+F9` in IntelliJ
Expected: Clean compilation, no errors

- [ ] **Step 3: Test manually from terminal**

```bash
cd out/production/Livraison
java --enable-native-access=ALL-UNNAMED \
  -cp ".:$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar" \
  Main
```

Verify:
- Arrow-key navigation works in all menus
- Can add a new client (text input works)
- Can view client list (shows demo data + new client)
- Can search client by name
- Can create a commande (client selection menu works)
- Can affect livraison (commande + livreur + type selection)
- Can terminate a livraison
- Statistics display correctly

- [ ] **Step 4: Commit**

```bash
git add src/Main.java
git commit -m "feat: implement full interactive Main with CRUD, search, stats"
```

---

### Task 10: Update run.sh and final cleanup

**Files:**
- Modify: `run.sh`
- Modify: `ReadMe.md`

- [ ] **Step 1: Fix run.sh to use relative path**

```bash
#!/bin/bash
cd "$(dirname "$0")/out/production/Livraison"
java --enable-native-access=ALL-UNNAMED \
  -cp ".:$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar" \
  Main
```

- [ ] **Step 2: Update ReadMe.md structure section**

In `ReadMe.md`, update the "Structure du projet" section to match the new file list:

```
Livraison/
├── src/
│   ├── Client.java            # Entité client
│   ├── Livreur.java           # Entité livreur
│   ├── Commande.java          # Entité commande (client, description, statut)
│   ├── Livraison.java         # Entité livraison (commande, livreur, dates)
│   ├── StatutCommande.java    # Enum : EN_ATTENTE, EN_PREPARATION, EN_LIVRAISON, LIVREE
│   ├── TypeLivraison.java     # Enum : STANDARD, EXPRESS
│   ├── ServiceLivraison.java  # Logique métier (CRUD, recherche, tri, stats)
│   ├── MenuCLI.java           # Interface terminal (JLine, couleurs, navigation)
│   └── Main.java              # Point d'entrée, menus et interactions
├── run.sh                     # Script de lancement
└── README.md
```

- [ ] **Step 3: Build, run full end-to-end test**

Test all spec requirements:
1. Clients: ajouter, supprimer, modifier, rechercher (id + nom), afficher, tri par nom
2. Livreurs: ajouter, supprimer, modifier, rechercher (id + nom), afficher
3. Commandes: créer pour un client, supprimer, rechercher par id, afficher, tri par date, commandes d'un client, commandes en livraison
4. Livraisons: affecter (avec type express/standard), terminer, en cours, historique
5. Statistiques: compteurs, livreur le plus actif, activité par livreur

- [ ] **Step 4: Commit**

```bash
git add run.sh ReadMe.md
git commit -m "chore: update run.sh and ReadMe to match new project structure"
```

---

## Self-Review Checklist

### Spec coverage

| Spec Requirement | Task |
|---|---|
| 3.1 Ajouter client | Task 9 - ajouterClient() |
| 3.1 Supprimer client | Task 9 - supprimerClient() |
| 3.1 Modifier client | Task 9 - modifierClient() |
| 3.1 Afficher clients | Task 9 - afficherClients() |
| 3.1 Rechercher client (id/nom) | Task 9 - rechercherClient() |
| 3.2 Ajouter livreur | Task 9 - ajouterLivreur() |
| 3.2 Supprimer livreur | Task 9 - supprimerLivreur() |
| 3.2 Modifier livreur | Task 9 - modifierLivreur() |
| 3.2 Afficher livreurs | Task 9 - afficherLivreurs() |
| 3.2 Rechercher livreur (id/nom) | Task 9 - rechercherLivreur() |
| 3.3 Créer commande pour client | Task 9 - creerCommande() |
| 3.3 Supprimer commande | Task 9 - supprimerCommande() |
| 3.3 Afficher commandes | Task 9 - afficherCommandes() |
| 3.3 Rechercher commande par id | Task 9 - rechercherCommande() |
| 3.3 Statuts (4 types) | Task 3 - StatutCommande enum |
| 3.4 Affecter commande à livreur | Task 9 - affecterLivraison() |
| 3.4 Enregistrer livraison | Task 9 - terminerLivraison() |
| 3.4 Livraisons en cours | Task 9 - afficherLivraisons(enCours) |
| 3.4 Historique livraisons | Task 9 - afficherLivraisons(terminees) |
| 3.5 Affichage tous clients/livreurs/commandes | Task 9 - menus |
| 3.5 Commandes en livraison | Task 9 - commandesEnLivraison |
| 3.5 Livraisons terminées | Task 9 - historique |
| **Avancé**: Tri clients par nom | Task 7 + Task 9 |
| **Avancé**: Tri commandes par date | Task 7 + Task 9 |
| **Avancé**: Stats livreurs actifs | Task 7 + Task 9 |
| **Avancé**: Commandes d'un client | Task 9 - commandesParClient() |
| **Avancé**: Types livraison (express/standard) | Task 4 + Task 6 + Task 9 |
| 6. Classes: Client, Livreur, Commande, Livraison, ServiceLivraison, Main | All tasks |

**5 advanced features implemented** (spec requires minimum 2).

### Type consistency
- `Client.getPrenom()` used consistently (not getSurname)
- `Livreur.getVehicule()` used consistently (not getVoiture/getPlaque)
- `StatutCommande` enum values referenced identically in Commande, Livraison, ServiceLivraison, Main
- `TypeLivraison` enum referenced identically in Livraison and Main
- All `toString()` methods return formats used by Main's selection menus
