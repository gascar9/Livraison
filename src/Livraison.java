import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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

    // ═══════════════════════════════════════
    //           MENU LIVRAISONS
    // ═══════════════════════════════════════

    public static void menu(ServiceLivraison service, MenuCLI cli) throws IOException {
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
                case 0 -> affecter(service, cli);
                case 1 -> terminer(service, cli);
                case 2 -> afficherListe(service.getLivraisonsEnCours(), "Livraisons en cours", cli);
                case 3 -> afficherListe(service.getLivraisonsTerminees(), "Historique des livraisons", cli);
                case 4 -> back = true;
            }
        }
    }

    private static void affecter(ServiceLivraison service, MenuCLI cli) throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Affecter une Livraison");

        ArrayList<Commande> affectables = new ArrayList<>();
        for (Commande c : service.getListeCommandes()) {
            if ((c.getStatut() == StatutCommande.EN_ATTENTE || c.getStatut() == StatutCommande.EN_PREPARATION)
                && !service.commandeDejaAffectee(c.getId())) {
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

        String[] optionsCmd = new String[affectables.size() + 1];
        for (int i = 0; i < affectables.size(); i++) {
            optionsCmd[i] = affectables.get(i).toString();
        }
        optionsCmd[affectables.size()] = "Annuler";
        int choixCmd = cli.selectionner("Choisir la commande", optionsCmd);
        if (choixCmd == affectables.size()) return;

        List<Livreur> livreurs = service.getListeLivreurs();
        String[] optionsLiv = new String[livreurs.size() + 1];
        for (int i = 0; i < livreurs.size(); i++) {
            optionsLiv[i] = livreurs.get(i).toString();
        }
        optionsLiv[livreurs.size()] = "Annuler";
        int choixLiv = cli.selectionner("Choisir le livreur", optionsLiv);
        if (choixLiv == livreurs.size()) return;

        int choixType = cli.selectionner("Type de livraison", new String[]{"Standard", "Express"});
        TypeLivraison typeLiv = (choixType == 0) ? TypeLivraison.STANDARD : TypeLivraison.EXPRESS;

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

        Livraison livraison = new Livraison(affectables.get(choixCmd), livreurs.get(choixLiv), datePrevue, typeLiv);
        service.affecterLivraison(livraison);
        cli.afficherSucces("Livraison #" + livraison.getId() + " affectee (" + typeLiv.getLabel() + ")");
        cli.pause();
    }

    private static void terminer(ServiceLivraison service, MenuCLI cli) throws IOException {
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

        Livraison liv = enCours.get(choix);
        liv.terminerLivraison();
        cli.afficherSucces("Livraison #" + liv.getId() + " terminee - Commande #"
                + liv.getCommande().getId() + " livree a " + liv.getCommande().getClient().getNom());
        cli.pause();
    }

    private static void afficherListe(ArrayList<Livraison> livraisons, String titre, MenuCLI cli) throws IOException {
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
}
