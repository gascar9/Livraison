import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestionClients {
    private ServiceLivraison service;
    private MenuCLI cli;

    public GestionClients(ServiceLivraison service, MenuCLI cli) {
        this.service = service;
        this.cli = cli;
    }

    public void menu() throws IOException {
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
                case 0 -> ajouter();
                case 1 -> supprimer();
                case 2 -> modifier();
                case 3 -> rechercher();
                case 4 -> afficherListe(new ArrayList<>(service.getListeClients()), "Liste des Clients");
                case 5 -> afficherListe(service.getClientsTriesParNom(), "Clients tries par nom");
                case 6 -> back = true;
            }
        }
    }

    private void ajouter() throws IOException {
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

    private void supprimer() throws IOException {
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
            cli.pause();
            return;
        }

        client.afficherDetails();

        if (!service.peutSupprimerClient(id)) {
            cli.afficherErreur("Ce client a des commandes en cours. Impossible de le supprimer.");
            cli.pause();
            return;
        }

        if (cli.confirmer("Confirmer la suppression de " + client.getNom() + " " + client.getPrenom() + " ?")) {
            service.supprimerClient(id);
            cli.afficherSucces("Client #" + id + " supprime.");
        } else {
            cli.afficherInfo("Suppression annulee.");
        }
        cli.pause();
    }

    private void modifier() throws IOException {
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

    private void rechercher() throws IOException {
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
                afficherListe(resultats, "Resultats (" + resultats.size() + ")");
            }
        }
        cli.pause();
    }

    private void afficherListe(ArrayList<Client> clients, String titre) throws IOException {
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
}
