import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // ═══════════════════════════════════════
    //            MENU COMMANDES
    // ═══════════════════════════════════════

    public static void menu(ServiceLivraison service, MenuCLI cli) throws IOException {
        boolean back = false;
        while (!back) {
            int choix = cli.selectionner("GESTION DES COMMANDES", new String[]{
                    "Creer une commande",
                    "Supprimer une commande",
                    "Modifier le statut d'une commande",
                    "Rechercher une commande",
                    "Afficher toutes les commandes",
                    "Commandes triees par date",
                    "Commandes d'un client",
                    "Commandes en cours de livraison",
                    "Retour"
            });

            switch (choix) {
                case 0 -> creer(service, cli);
                case 1 -> supprimer(service, cli);
                case 2 -> modifierStatutMenu(service, cli);
                case 3 -> rechercher(service, cli);
                case 4 -> afficherListe(new ArrayList<>(service.getListeCommandes()), "Toutes les Commandes", cli);
                case 5 -> afficherListe(service.getCommandesTrieesParDate(), "Commandes triees par date", cli);
                case 6 -> commandesParClient(service, cli);
                case 7 -> afficherListe(service.getCommandesEnLivraison(), "Commandes en livraison", cli);
                case 8 -> back = true;
            }
        }
    }

    private static void creer(ServiceLivraison service, MenuCLI cli) throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Nouvelle Commande");

        if (service.getListeClients().isEmpty()) {
            cli.afficherErreur("Aucun client. Ajoutez un client d'abord.");
            cli.pause();
            return;
        }

        List<Client> clients = service.getListeClients();
        String[] options = new String[clients.size() + 1];
        for (int i = 0; i < clients.size(); i++) {
            options[i] = clients.get(i).toString();
        }
        options[clients.size()] = "Annuler";

        int choix = cli.selectionner("Choisir le client", options);
        if (choix == clients.size()) return;

        cli.clearScreen();
        cli.afficherTitre("Nouvelle Commande pour " + clients.get(choix).getNom());
        String desc = cli.lireTexte("Description");

        if (desc.isEmpty()) {
            cli.afficherErreur("Description obligatoire.");
            cli.pause();
            return;
        }

        Commande commande = new Commande(clients.get(choix), desc);
        service.creerCommande(commande);
        cli.afficherSucces("Commande #" + commande.getId() + " creee.");
        cli.pause();
    }

    private static void supprimer(ServiceLivraison service, MenuCLI cli) throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Supprimer une Commande");
        int id = cli.lireEntier("ID de la commande");
        Commande commande = service.rechercherCommandeParId(id);
        if (commande == null) {
            cli.afficherErreur("Commande #" + id + " introuvable.");
            cli.pause();
            return;
        }

        commande.afficherCommande();

        if (!service.peutSupprimerCommande(id)) {
            cli.afficherErreur("Cette commande a une livraison en cours. Impossible de la supprimer.");
            cli.pause();
            return;
        }

        if (cli.confirmer("Confirmer la suppression de la commande #" + id + " ?")) {
            service.supprimerCommande(id);
            cli.afficherSucces("Commande #" + id + " supprimee.");
        } else {
            cli.afficherInfo("Suppression annulee.");
        }
        cli.pause();
    }

    private static void modifierStatutMenu(ServiceLivraison service, MenuCLI cli) throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Modifier le statut d'une commande");

        if (service.getListeCommandes().isEmpty()) {
            cli.afficherErreur("Aucune commande.");
            cli.pause();
            return;
        }

        List<Commande> commandes = service.getListeCommandes();
        String[] options = new String[commandes.size() + 1];
        for (int i = 0; i < commandes.size(); i++) {
            options[i] = commandes.get(i).toString();
        }
        options[commandes.size()] = "Annuler";

        int choixCmd = cli.selectionner("Choisir la commande", options);
        if (choixCmd == commandes.size()) return;

        Commande commande = commandes.get(choixCmd);
        int choixStatut = cli.selectionner("Nouveau statut pour commande #" + commande.getId(), new String[]{
                "En attente", "En preparation", "En livraison", "Livree", "Annuler"
        });

        switch (choixStatut) {
            case 0 -> commande.modifierStatut(StatutCommande.EN_ATTENTE);
            case 1 -> commande.modifierStatut(StatutCommande.EN_PREPARATION);
            case 2 -> commande.modifierStatut(StatutCommande.EN_LIVRAISON);
            case 3 -> commande.modifierStatut(StatutCommande.LIVREE);
            case 4 -> { return; }
        }

        cli.afficherSucces("Commande #" + commande.getId() + " -> " + commande.getStatut().getLabel());
        cli.pause();
    }

    private static void rechercher(ServiceLivraison service, MenuCLI cli) throws IOException {
        int choix = cli.selectionner("Rechercher par", new String[]{
                "Identifiant", "Description", "Retour"
        });
        if (choix == 2) return;

        cli.clearScreen();
        if (choix == 0) {
            int id = cli.lireEntier("ID de la commande");
            Commande commande = service.rechercherCommandeParId(id);
            if (commande == null) {
                cli.afficherErreur("Commande #" + id + " introuvable.");
            } else {
                System.out.println();
                commande.afficherCommande();
            }
        } else {
            String texte = cli.lireTexte("Description a rechercher");
            ArrayList<Commande> resultats = service.rechercherCommandeParDescription(texte);
            if (resultats.isEmpty()) {
                cli.afficherErreur("Aucune commande trouvee pour \"" + texte + "\".");
            } else {
                afficherListe(resultats, "Resultats (" + resultats.size() + ")", cli);
            }
        }
        cli.pause();
    }

    private static void commandesParClient(ServiceLivraison service, MenuCLI cli) throws IOException {
        List<Client> clients = service.getListeClients();
        if (clients.isEmpty()) {
            cli.afficherErreur("Aucun client.");
            cli.pause();
            return;
        }

        String[] options = new String[clients.size() + 1];
        for (int i = 0; i < clients.size(); i++) {
            options[i] = clients.get(i).toString();
        }
        options[clients.size()] = "Annuler";

        int choix = cli.selectionner("Choisir le client", options);
        if (choix == clients.size()) return;

        Client client = (Client) clients.get(choix);
        ArrayList<Commande> commandes = service.getCommandesParClient(client);
        afficherListe(commandes, "Commandes de " + client.getNom() + " " + client.getPrenom(), cli);
    }

    private static void afficherListe(ArrayList<Commande> commandes, String titre, MenuCLI cli) throws IOException {
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
}
