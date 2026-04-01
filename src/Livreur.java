import java.io.IOException;
import java.util.ArrayList;

public class Livreur extends Personne {
    private String vehicule;
    private static int compteur = 1;

    public Livreur(String nom, String prenom, String telephone, String vehicule) {
        super(compteur++, nom, prenom, telephone);
        this.vehicule = vehicule;
    }

    @Override
    public void afficherDetails() {
        System.out.println("  +-----------------------------+");
        System.out.printf("  | Livreur #%-18d |%n", getId());
        System.out.printf("  | Nom:       %-17s |%n", getNom() + " " + getPrenom());
        System.out.printf("  | Telephone: %-17s |%n", getTelephone());
        System.out.printf("  | Vehicule:  %-17s |%n", vehicule);
        System.out.println("  +-----------------------------+");
    }

    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }

    @Override
    public String toString() {
        return "#" + getId() + " " + getNom() + " " + getPrenom() + " (" + vehicule + ")";
    }

    // ═══════════════════════════════════════
    //             MENU LIVREURS
    // ═══════════════════════════════════════

    public static void menu(ServiceLivraison service, MenuCLI cli) throws IOException {
        boolean back = false;
        while (!back) {
            int choix = cli.selectionner("GESTION DES LIVREURS", new String[]{
                    "Ajouter un livreur",
                    "Supprimer un livreur",
                    "Modifier un livreur",
                    "Rechercher un livreur",
                    "Afficher tous les livreurs",
                    "Afficher livreurs tries par nom",
                    "Retour"
            });

            switch (choix) {
                case 0 -> ajouter(service, cli);
                case 1 -> supprimer(service, cli);
                case 2 -> modifier(service, cli);
                case 3 -> rechercher(service, cli);
                case 4 -> afficherListe(new ArrayList<>(service.getListeLivreurs()), "Liste des Livreurs", cli);
                case 5 -> afficherListe(service.getLivreursTriesParNom(), "Livreurs tries par nom", cli);
                case 6 -> back = true;
            }
        }
    }

    private static void ajouter(ServiceLivraison service, MenuCLI cli) throws IOException {
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

    private static void supprimer(ServiceLivraison service, MenuCLI cli) throws IOException {
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
            cli.pause();
            return;
        }

        livreur.afficherDetails();

        if (!service.peutSupprimerLivreur(id)) {
            cli.afficherErreur("Ce livreur a des livraisons en cours. Impossible de le supprimer.");
            cli.pause();
            return;
        }

        if (cli.confirmer("Confirmer la suppression de " + livreur.getNom() + " " + livreur.getPrenom() + " ?")) {
            service.supprimerLivreur(id);
            cli.afficherSucces("Livreur #" + id + " supprime.");
        } else {
            cli.afficherInfo("Suppression annulee.");
        }
        cli.pause();
    }

    private static void modifier(ServiceLivraison service, MenuCLI cli) throws IOException {
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
        String vehiculeInput = cli.lireTexte("Vehicule [" + livreur.getVehicule() + "]");
        if (!vehiculeInput.isEmpty()) livreur.setVehicule(vehiculeInput);

        cli.afficherSucces("Livreur #" + id + " modifie.");
        cli.pause();
    }

    private static void rechercher(ServiceLivraison service, MenuCLI cli) throws IOException {
        int choix = cli.selectionner("Rechercher par", new String[]{
                "Identifiant", "Nom", "Retour"
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
                afficherListe(resultats, "Resultats (" + resultats.size() + ")", cli);
            }
        }
        cli.pause();
    }

    private static void afficherListe(ArrayList<Livreur> livreurs, String titre, MenuCLI cli) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
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
}
