import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;
import java.io.IOException;

public class Main {

    // ─── Couleurs ───
    static final String RESET = "\u001B[0m";
    static final String BOLD  = "\u001B[1m";
    static final String DIM   = "\u001B[2m";
    static final String VERT  = "\u001B[32m";
    static final String ROUGE = "\u001B[31m";
    static final String JAUNE = "\u001B[33m";
    static final String CYAN  = "\u001B[36m";

    static Terminal terminal;
    static NonBlockingReader reader;

    public static void main(String[] args) throws Exception {
        // ─── Init JLine ───
        terminal = TerminalBuilder.builder()
                .system(true)
                .jansi(true)
                .build();
        terminal.enterRawMode();
        reader = terminal.reader();

        // ─── Boucle principale ───
        boolean running = true;
        while (running) {
            int choix = selectionner("🚚  SYSTÈME DE LIVRAISON", new String[]{
                    "👤  Gestion des clients",
                    "🏍   Gestion des livreurs",
                    "📦  Gestion des commandes",
                    "🚚  Gestion des livraisons",
                    "📊  Statistiques",
                    "❌  Quitter"
            });

            switch (choix) {
                case 0 -> demoClients();
                case 1 -> demoSousMenu("🏍   GESTION DES LIVREURS", new String[]{
                        "➕  Ajouter un livreur",
                        "🗑   Supprimer un livreur",
                        "📋  Afficher les livreurs",
                        "←   Retour"
                });
                case 2 -> demoSousMenu("📦  GESTION DES COMMANDES", new String[]{
                        "➕  Créer une commande",
                        "🗑   Supprimer une commande",
                        "🔍  Rechercher une commande",
                        "📋  Afficher les commandes",
                        "←   Retour"
                });
                case 3 -> demoSousMenu("🚚  GESTION DES LIVRAISONS", new String[]{
                        "📌  Affecter une livraison",
                        "✅  Terminer une livraison",
                        "📋  Livraisons en cours",
                        "📜  Historique",
                        "←   Retour"
                });
                case 4 -> demoStats();
                case 5 -> running = false;
            }
        }

        clearScreen();
        System.out.println(VERT + BOLD + "\n  ✅ À bientôt !\n" + RESET);
        terminal.close();
    }

    // ═══════════════════════════════════════
    //          DÉMOS DE TEST
    // ═══════════════════════════════════════

    static void demoClients() throws IOException {
        boolean back = false;
        while (!back) {
            int choix = selectionner("👤  GESTION DES CLIENTS", new String[]{
                    "➕  Ajouter un client",
                    "🗑   Supprimer un client",
                    "✏️   Modifier un client",
                    "🔍  Rechercher un client",
                    "📋  Afficher tous les clients",
                    "←   Retour"
            });

            switch (choix) {
                case 0 -> {
                    clearScreen();
                    System.out.println(JAUNE + BOLD + "\n  ── Nouveau Client ──\n" + RESET);
                    System.out.println(VERT + "  ✅ Client #1 créé : Jean Dupont" + RESET);
                    System.out.println(DIM + "     📞 06.12.34.56.78" + RESET);
                    System.out.println(DIM + "     ✉️  jean@mail.com" + RESET);
                    System.out.println(DIM + "     🏠 12 rue de Lyon" + RESET);
                    pause();
                }
                case 1 -> {
                    clearScreen();
                    System.out.println(ROUGE + "\n  🗑  Client #1 supprimé" + RESET);
                    pause();
                }
                case 2 -> {
                    clearScreen();
                    System.out.println(JAUNE + "\n  ✏️  Client #1 modifié : nouveau tel = 07.99.88.77.66" + RESET);
                    pause();
                }
                case 3 -> {
                    clearScreen();
                    System.out.println(CYAN + "\n  🔍 Résultat : Client #1 - Jean Dupont" + RESET);
                    pause();
                }
                case 4 -> {
                    clearScreen();
                    afficherTableauClients();
                    pause();
                }
                case 5 -> back = true;
            }
        }
    }

    static void afficherTableauClients() {
        System.out.println(JAUNE + BOLD + "\n  ── Liste des Clients ──\n" + RESET);
        System.out.println(CYAN + "  ┌──────┬─────────────────┬─────────────────┬──────────────┬─────────────────────┐" + RESET);
        System.out.printf(CYAN +  "  │ %-4s │ %-15s │ %-15s │ %-12s │ %-19s │%n" + RESET,
                "ID", "Nom", "Prénom", "Téléphone", "Email");
        System.out.println(CYAN + "  ├──────┼─────────────────┼─────────────────┼──────────────┼─────────────────────┤" + RESET);

        // Données de test
        String[][] clients = {
                {"1", "Dupont",  "Jean",    "06.12.34.56", "jean@mail.com"},
                {"2", "Martin",  "Claire",  "07.65.43.21", "claire@mail.com"},
                {"3", "Bernard", "Lucas",   "06.98.76.54", "lucas@mail.com"},
                {"4", "Petit",   "Emma",    "07.11.22.33", "emma@mail.com"},
        };

        for (String[] c : clients) {
            System.out.printf("  │ %-4s │ %-15s │ %-15s │ %-12s │ %-19s │%n",
                    c[0], c[1], c[2], c[3], c[4]);
        }

        System.out.println(CYAN + "  └──────┴─────────────────┴─────────────────┴──────────────┴─────────────────────┘" + RESET);
        System.out.println(DIM + "\n  Total : 4 clients" + RESET);
    }

    static void demoStats() throws IOException {
        clearScreen();
        System.out.println(JAUNE + BOLD + "\n  ── Statistiques ──\n" + RESET);
        System.out.println("  👤 Clients          :  " + CYAN + "4" + RESET);
        System.out.println("  🏍  Livreurs         :  " + CYAN + "3" + RESET);
        System.out.println("  📦 Commandes        :  " + CYAN + "12" + RESET);
        System.out.println("  🚚 Livraisons       :  " + CYAN + "8" + RESET);
        System.out.println("  ✅ Livrées          :  " + VERT + "6" + RESET);
        System.out.println("  ⏳ En cours         :  " + JAUNE + "2" + RESET);
        System.out.println();
        System.out.println("  🏆 Livreur + actif  :  " + BOLD + "Pierre Martin (4 livraisons)" + RESET);
        pause();
    }

    static void demoSousMenu(String titre, String[] options) throws IOException {
        boolean back = false;
        while (!back) {
            int choix = selectionner(titre, options);
            if (choix == options.length - 1) {
                back = true;
            } else {
                clearScreen();
                System.out.println(VERT + "\n  ✅ Action \"" + options[choix] + "\" exécutée (démo)" + RESET);
                pause();
            }
        }
    }

    // ═══════════════════════════════════════
    //       NAVIGATION FLÈCHES (JLine)
    // ═══════════════════════════════════════

    static int selectionner(String titre, String[] options) throws IOException {
        int selection = 0;
        boolean choisi = false;

        while (!choisi) {
            clearScreen();

            System.out.println();
            System.out.println(CYAN + BOLD + "  ╔══════════════════════════════════════════╗");
            System.out.printf( "  ║  %-40s ║%n", titre);
            System.out.println("  ╚══════════════════════════════════════════╝" + RESET);
            System.out.println();

            for (int i = 0; i < options.length; i++) {
                if (i == selection) {
                    System.out.println(CYAN + BOLD + "   ▸ " + options[i] + RESET);
                } else {
                    System.out.println(DIM +       "     " + options[i] + RESET);
                }
            }

            System.out.println();
            System.out.println(DIM + "  ↑↓ Naviguer  │  Entrée Valider" + RESET);

            int key = reader.read();

            if (key == 27) {
                int next = reader.read();
                if (next == '[') {
                    int arrow = reader.read();
                    switch (arrow) {
                        case 'A' -> selection = (selection - 1 + options.length) % options.length;
                        case 'B' -> selection = (selection + 1) % options.length;
                    }
                }
            } else if (key == 13 || key == 10) {
                choisi = true;
            }
        }
        return selection;
    }

    // ═══════════════════════════════════════
    //            UTILITAIRES
    // ═══════════════════════════════════════

    static void pause() throws IOException {
        System.out.println(DIM + "\n  Appuyez sur une touche..." + RESET);
        reader.read();
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}