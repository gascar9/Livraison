import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        MenuCLI cli = new MenuCLI();
        ServiceLivraison service = new ServiceLivraison();

        // Donnees initiales
        chargerDonnees(service);

        // Gestionnaires de chaque module
        GestionClients gClients = new GestionClients(service, cli);
        GestionLivreurs gLivreurs = new GestionLivreurs(service, cli);
        GestionCommandes gCommandes = new GestionCommandes(service, cli);
        GestionLivraisons gLivraisons = new GestionLivraisons(service, cli);

        // Boucle principale
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
                case 0 -> gClients.menu();
                case 1 -> gLivreurs.menu();
                case 2 -> gCommandes.menu();
                case 3 -> gLivraisons.menu();
                case 4 -> afficherStatistiques(service, cli);
                case 5 -> running = false;
            }
        }

        cli.clearScreen();
        System.out.println(MenuCLI.VERT + MenuCLI.BOLD + "\n  A bientot !\n" + MenuCLI.RESET);
        cli.close();
    }

    private static void chargerDonnees(ServiceLivraison service) {
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

    private static void afficherStatistiques(ServiceLivraison service, MenuCLI cli) throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Statistiques");

        System.out.println("  Clients          :  " + MenuCLI.CYAN + service.getListeClients().size() + MenuCLI.RESET);
        System.out.println("  Livreurs         :  " + MenuCLI.CYAN + service.getListeLivreurs().size() + MenuCLI.RESET);
        System.out.println("  Commandes        :  " + MenuCLI.CYAN + service.getListeCommandes().size() + MenuCLI.RESET);
        System.out.println("  Livraisons       :  " + MenuCLI.CYAN + service.getListeLivraisons().size() + MenuCLI.RESET);
        System.out.println("  Livrees          :  " + MenuCLI.VERT + service.getNombreCommandesLivrees() + MenuCLI.RESET);
        System.out.println("  En cours         :  " + MenuCLI.JAUNE + service.getLivraisonsEnCours().size() + MenuCLI.RESET);
        System.out.println();

        Livreur meilleur = service.getLivreurPlusActif();
        if (meilleur != null) {
            int nb = service.getNombreLivraisonsParLivreur(meilleur);
            System.out.println("  Livreur + actif  :  " + MenuCLI.BOLD + meilleur.getNom() + " " + meilleur.getPrenom()
                    + " (" + nb + " livraison(s))" + MenuCLI.RESET);
        } else {
            System.out.println(MenuCLI.DIM + "  Aucun livreur actif." + MenuCLI.RESET);
        }

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
