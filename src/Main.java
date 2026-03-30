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

        // Donnees initiales
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
                    "Modifier le statut d'une commande",
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
                case 2 -> modifierStatutCommande();
                case 3 -> rechercherCommande();
                case 4 -> afficherCommandes(service.getListeCommandes(), "Toutes les Commandes");
                case 5 -> afficherCommandes(service.getCommandesTrieesParDate(), "Commandes triees par date");
                case 6 -> commandesParClient();
                case 7 -> afficherCommandes(service.getCommandesEnLivraison(), "Commandes en livraison");
                case 8 -> back = true;
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

    static void modifierStatutCommande() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Modifier le statut d'une commande");

        if (service.getListeCommandes().isEmpty()) {
            cli.afficherErreur("Aucune commande.");
            cli.pause();
            return;
        }

        ArrayList<Commande> commandes = service.getListeCommandes();
        String[] optionsCmd = new String[commandes.size() + 1];
        for (int i = 0; i < commandes.size(); i++) {
            optionsCmd[i] = commandes.get(i).toString();
        }
        optionsCmd[commandes.size()] = "Annuler";

        int choixCmd = cli.selectionner("Choisir la commande", optionsCmd);
        if (choixCmd == commandes.size()) return;

        Commande commande = commandes.get(choixCmd);
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

        String[] optionsCmd = new String[affectables.size() + 1];
        for (int i = 0; i < affectables.size(); i++) {
            optionsCmd[i] = affectables.get(i).toString();
        }
        optionsCmd[affectables.size()] = "Annuler";

        int choixCmd = cli.selectionner("Choisir la commande", optionsCmd);
        if (choixCmd == affectables.size()) return;

        ArrayList<Livreur> livreurs = service.getListeLivreurs();
        String[] optionsLiv = new String[livreurs.size() + 1];
        for (int i = 0; i < livreurs.size(); i++) {
            optionsLiv[i] = livreurs.get(i).toString();
        }
        optionsLiv[livreurs.size()] = "Annuler";

        int choixLiv = cli.selectionner("Choisir le livreur", optionsLiv);
        if (choixLiv == livreurs.size()) return;

        int choixType = cli.selectionner("Type de livraison", new String[]{
                "Standard",
                "Express"
        });
        TypeLivraison type = (choixType == 0) ? TypeLivraison.STANDARD : TypeLivraison.EXPRESS;

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

        Livreur meilleur = service.getLivreurPlusActif();
        if (meilleur != null) {
            int nbLivraisons = service.getNombreLivraisonsParLivreur(meilleur);
            System.out.println("  Livreur + actif  :  " + MenuCLI.BOLD + meilleur.getNom() + " " + meilleur.getPrenom()
                    + " (" + nbLivraisons + " livraison(s))" + MenuCLI.RESET);
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
