import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestionCommandes {
    private ServiceLivraison service;
    private MenuCLI cli;

    public GestionCommandes(ServiceLivraison service, MenuCLI cli) {
        this.service = service;
        this.cli = cli;
    }

    public void menu() throws IOException {
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
                case 0 -> creer();
                case 1 -> supprimer();
                case 2 -> modifierStatut();
                case 3 -> rechercher();
                case 4 -> afficherListe(new ArrayList<>(service.getListeCommandes()), "Toutes les Commandes");
                case 5 -> afficherListe(service.getCommandesTrieesParDate(), "Commandes triees par date");
                case 6 -> commandesParClient();
                case 7 -> afficherListe(service.getCommandesEnLivraison(), "Commandes en livraison");
                case 8 -> back = true;
            }
        }
    }

    private void creer() throws IOException {
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
        String description = cli.lireTexte("Description");

        if (description.isEmpty()) {
            cli.afficherErreur("Description obligatoire.");
            cli.pause();
            return;
        }

        Commande commande = new Commande(clients.get(choix), description);
        service.creerCommande(commande);
        cli.afficherSucces("Commande #" + commande.getId() + " creee.");
        cli.pause();
    }

    private void supprimer() throws IOException {
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

    private void modifierStatut() throws IOException {
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

        int choix = cli.selectionner("Choisir la commande", options);
        if (choix == commandes.size()) return;

        Commande commande = commandes.get(choix);
        int choixStatut = cli.selectionner("Nouveau statut pour commande #" + commande.getId(), new String[]{
                "En attente",
                "En preparation",
                "En livraison",
                "Livree",
                "Annuler"
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

    private void rechercher() throws IOException {
        int choix = cli.selectionner("Rechercher par", new String[]{
                "Identifiant",
                "Description",
                "Retour"
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
                afficherListe(resultats, "Resultats (" + resultats.size() + ")");
            }
        }
        cli.pause();
    }

    private void commandesParClient() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Commandes d'un Client");

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
        afficherListe(commandes, "Commandes de " + client.getNom() + " " + client.getPrenom());
    }

    private void afficherListe(ArrayList<Commande> commandes, String titre) throws IOException {
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
