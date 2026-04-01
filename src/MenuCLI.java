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
                // consume escape sequences
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

    public boolean confirmer(String message) throws IOException {
        terminal.enterRawMode();
        System.out.println(JAUNE + "\n  " + message + " (o/n)" + RESET);
        System.out.flush();
        while (true) {
            int key = reader().read();
            if (key == 'o' || key == 'O' || key == 13 || key == 10) return true;
            if (key == 'n' || key == 'N') return false;
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
