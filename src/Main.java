import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static MenuCLI cli;
    static ServiceLivraison service = new ServiceLivraison();

    public static void main(String[] args) throws Exception {
        cli = new MenuCLI();
        chargerDonnees();

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

    static void chargerDonnees() {
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
                    "Ajouter un client", "Supprimer un client", "Modifier un client",
                    "Rechercher un client", "Afficher tous les clients",
                    "Afficher clients tries par nom", "Retour"
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
        if (service.getListeClients().isEmpty()) { cli.afficherErreur("Aucun client."); cli.pause(); return; }

        int id = cli.lireEntier("ID du client a supprimer");
        Client client = service.rechercherClientParId(id);
        if (client == null) { cli.afficherErreur("Client #" + id + " introuvable."); cli.pause(); return; }

        client.afficherDetails();
        if (!service.peutSupprimerClient(id)) {
            cli.afficherErreur("Ce client a des commandes/livraisons en cours. Impossible de le supprimer.");
            cli.pause();
            return;
        }

        if (cli.confirmer("Confirmer la suppression de " + client.getNom() + " " + client.getPrenom() + " ?")) {
            service.supprimerClient(id);
            cli.afficherSucces("Client #" + id + " supprime (commandes et livraisons associees supprimees).");
        } else {
            cli.afficherInfo("Suppression annulee.");
        }
        cli.pause();
    }

    static void modifierClient() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Modifier un Client");
        int id = cli.lireEntier("ID du client a modifier");
        Client client = service.rechercherClientParId(id);
        if (client == null) { cli.afficherErreur("Client #" + id + " introuvable."); cli.pause(); return; }

        client.afficherDetails();
        System.out.println(MenuCLI.DIM + "  (laisser vide pour ne pas modifier)\n" + MenuCLI.RESET);

        String v;
        v = cli.lireTexte("Nom [" + client.getNom() + "]");       if (!v.isEmpty()) client.setNom(v);
        v = cli.lireTexte("Prenom [" + client.getPrenom() + "]");  if (!v.isEmpty()) client.setPrenom(v);
        v = cli.lireTexte("Telephone [" + client.getTelephone() + "]"); if (!v.isEmpty()) client.setTelephone(v);
        v = cli.lireTexte("Email [" + client.getEmail() + "]");    if (!v.isEmpty()) client.setEmail(v);
        v = cli.lireTexte("Adresse [" + client.getAdresse() + "]"); if (!v.isEmpty()) client.setAdresse(v);

        cli.afficherSucces("Client #" + id + " modifie.");
        cli.pause();
    }

    static void rechercherClient() throws IOException {
        int choix = cli.selectionner("Rechercher par", new String[]{"Identifiant", "Nom", "Retour"});
        if (choix == 2) return;
        cli.clearScreen();
        if (choix == 0) {
            int id = cli.lireEntier("ID du client");
            Client c = service.rechercherClientParId(id);
            if (c == null) cli.afficherErreur("Client #" + id + " introuvable.");
            else { System.out.println(); c.afficherDetails(); }
        } else {
            String nom = cli.lireTexte("Nom a rechercher");
            List<Client> r = service.rechercherClientParNom(nom);
            if (r.isEmpty()) cli.afficherErreur("Aucun client trouve pour \"" + nom + "\".");
            else afficherClients(r, "Resultats (" + r.size() + ")");
        }
        cli.pause();
    }

    static void afficherClients(List<Client> clients, String titre) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
        if (clients.isEmpty()) { cli.afficherInfo("Aucun client."); }
        else {
            System.out.printf(MenuCLI.CYAN + "  %-5s %-15s %-15s %-15s %-20s%n" + MenuCLI.RESET, "ID", "Nom", "Prenom", "Telephone", "Email");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(70) + MenuCLI.RESET);
            for (Client c : clients)
                System.out.printf("  %-5d %-15s %-15s %-15s %-20s%n", c.getId(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getEmail());
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
                    "Ajouter un livreur", "Supprimer un livreur", "Modifier un livreur",
                    "Rechercher un livreur", "Afficher tous les livreurs",
                    "Afficher livreurs tries par nom", "Retour"
            });
            switch (choix) {
                case 0 -> ajouterLivreur();
                case 1 -> supprimerLivreur();
                case 2 -> modifierLivreur();
                case 3 -> rechercherLivreur();
                case 4 -> afficherLivreurs(service.getListeLivreurs(), "Liste des Livreurs");
                case 5 -> afficherLivreurs(service.getLivreursTriesParNom(), "Livreurs tries par nom");
                case 6 -> back = true;
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

        if (nom.isEmpty() || prenom.isEmpty()) { cli.afficherErreur("Nom et prenom obligatoires."); cli.pause(); return; }

        Livreur livreur = new Livreur(nom, prenom, tel, vehicule);
        service.ajouterLivreur(livreur);
        cli.afficherSucces("Livreur #" + livreur.getId() + " cree : " + nom + " " + prenom);
        cli.pause();
    }

    static void supprimerLivreur() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Supprimer un Livreur");
        if (service.getListeLivreurs().isEmpty()) { cli.afficherErreur("Aucun livreur."); cli.pause(); return; }

        int id = cli.lireEntier("ID du livreur a supprimer");
        Livreur livreur = service.rechercherLivreurParId(id);
        if (livreur == null) { cli.afficherErreur("Livreur #" + id + " introuvable."); cli.pause(); return; }

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

    static void modifierLivreur() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Modifier un Livreur");
        int id = cli.lireEntier("ID du livreur a modifier");
        Livreur livreur = service.rechercherLivreurParId(id);
        if (livreur == null) { cli.afficherErreur("Livreur #" + id + " introuvable."); cli.pause(); return; }

        livreur.afficherDetails();
        System.out.println(MenuCLI.DIM + "  (laisser vide pour ne pas modifier)\n" + MenuCLI.RESET);

        String v;
        v = cli.lireTexte("Nom [" + livreur.getNom() + "]");       if (!v.isEmpty()) livreur.setNom(v);
        v = cli.lireTexte("Prenom [" + livreur.getPrenom() + "]");  if (!v.isEmpty()) livreur.setPrenom(v);
        v = cli.lireTexte("Telephone [" + livreur.getTelephone() + "]"); if (!v.isEmpty()) livreur.setTelephone(v);
        v = cli.lireTexte("Vehicule [" + livreur.getVehicule() + "]");  if (!v.isEmpty()) livreur.setVehicule(v);

        cli.afficherSucces("Livreur #" + id + " modifie.");
        cli.pause();
    }

    static void rechercherLivreur() throws IOException {
        int choix = cli.selectionner("Rechercher par", new String[]{"Identifiant", "Nom", "Retour"});
        if (choix == 2) return;
        cli.clearScreen();
        if (choix == 0) {
            int id = cli.lireEntier("ID du livreur");
            Livreur l = service.rechercherLivreurParId(id);
            if (l == null) cli.afficherErreur("Livreur #" + id + " introuvable.");
            else { System.out.println(); l.afficherDetails(); }
        } else {
            String nom = cli.lireTexte("Nom a rechercher");
            List<Livreur> r = service.rechercherLivreurParNom(nom);
            if (r.isEmpty()) cli.afficherErreur("Aucun livreur trouve pour \"" + nom + "\".");
            else afficherLivreurs(r, "Resultats (" + r.size() + ")");
        }
        cli.pause();
    }

    static void afficherLivreurs(List<Livreur> livreurs, String titre) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
        if (livreurs.isEmpty()) { cli.afficherInfo("Aucun livreur."); }
        else {
            System.out.printf(MenuCLI.CYAN + "  %-5s %-15s %-15s %-15s %-15s%n" + MenuCLI.RESET, "ID", "Nom", "Prenom", "Telephone", "Vehicule");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(65) + MenuCLI.RESET);
            for (Livreur l : livreurs)
                System.out.printf("  %-5d %-15s %-15s %-15s %-15s%n", l.getId(), l.getNom(), l.getPrenom(), l.getTelephone(), l.getVehicule());
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
                    "Creer une commande", "Supprimer une commande", "Modifier le statut",
                    "Rechercher une commande", "Afficher toutes les commandes",
                    "Commandes triees par date", "Commandes d'un client",
                    "Commandes en cours de livraison", "Retour"
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
        List<Client> clients = service.getListeClients();
        if (clients.isEmpty()) { cli.afficherErreur("Aucun client. Ajoutez un client d'abord."); cli.pause(); return; }

        String[] opts = new String[clients.size() + 1];
        for (int i = 0; i < clients.size(); i++) opts[i] = clients.get(i).toString();
        opts[clients.size()] = "Annuler";

        int choix = cli.selectionner("Choisir le client", opts);
        if (choix == clients.size()) return;

        cli.clearScreen();
        cli.afficherTitre("Nouvelle Commande pour " + clients.get(choix).getNom());
        String desc = cli.lireTexte("Description");
        if (desc.isEmpty()) { cli.afficherErreur("Description obligatoire."); cli.pause(); return; }

        Commande cmd = new Commande(clients.get(choix), desc);
        service.creerCommande(cmd);
        cli.afficherSucces("Commande #" + cmd.getId() + " creee.");
        cli.pause();
    }

    static void supprimerCommande() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Supprimer une Commande");
        int id = cli.lireEntier("ID de la commande");
        Commande cmd = service.rechercherCommandeParId(id);
        if (cmd == null) { cli.afficherErreur("Commande #" + id + " introuvable."); cli.pause(); return; }

        cmd.afficherCommande();
        if (!service.peutSupprimerCommande(id)) {
            cli.afficherErreur("Cette commande a une livraison en cours. Impossible de la supprimer.");
            cli.pause();
            return;
        }

        if (cli.confirmer("Confirmer la suppression de la commande #" + id + " ?")) {
            service.supprimerCommande(id);
            cli.afficherSucces("Commande #" + id + " supprimee (livraisons associees supprimees).");
        } else {
            cli.afficherInfo("Suppression annulee.");
        }
        cli.pause();
    }

    static void modifierStatutCommande() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Modifier le statut d'une commande");
        List<Commande> commandes = service.getListeCommandes();
        if (commandes.isEmpty()) { cli.afficherErreur("Aucune commande."); cli.pause(); return; }

        String[] opts = new String[commandes.size() + 1];
        for (int i = 0; i < commandes.size(); i++) opts[i] = commandes.get(i).toString();
        opts[commandes.size()] = "Annuler";

        int choix = cli.selectionner("Choisir la commande", opts);
        if (choix == commandes.size()) return;

        Commande cmd = commandes.get(choix);
        int s = cli.selectionner("Nouveau statut pour commande #" + cmd.getId(), new String[]{
                "En attente", "En preparation", "En livraison", "Livree", "Annuler"
        });
        switch (s) {
            case 0 -> cmd.modifierStatut(StatutCommande.EN_ATTENTE);
            case 1 -> cmd.modifierStatut(StatutCommande.EN_PREPARATION);
            case 2 -> cmd.modifierStatut(StatutCommande.EN_LIVRAISON);
            case 3 -> cmd.modifierStatut(StatutCommande.LIVREE);
            case 4 -> { return; }
        }
        cli.afficherSucces("Commande #" + cmd.getId() + " -> " + cmd.getStatut().getLabel());
        cli.pause();
    }

    static void rechercherCommande() throws IOException {
        int choix = cli.selectionner("Rechercher par", new String[]{"Identifiant", "Description", "Retour"});
        if (choix == 2) return;
        cli.clearScreen();
        if (choix == 0) {
            int id = cli.lireEntier("ID de la commande");
            Commande c = service.rechercherCommandeParId(id);
            if (c == null) cli.afficherErreur("Commande #" + id + " introuvable.");
            else { System.out.println(); c.afficherCommande(); }
        } else {
            String texte = cli.lireTexte("Description a rechercher");
            List<Commande> r = service.rechercherCommandeParDescription(texte);
            if (r.isEmpty()) cli.afficherErreur("Aucune commande trouvee pour \"" + texte + "\".");
            else afficherCommandes(r, "Resultats (" + r.size() + ")");
        }
        cli.pause();
    }

    static void commandesParClient() throws IOException {
        List<Client> clients = service.getListeClients();
        if (clients.isEmpty()) { cli.afficherErreur("Aucun client."); cli.pause(); return; }

        String[] opts = new String[clients.size() + 1];
        for (int i = 0; i < clients.size(); i++) opts[i] = clients.get(i).toString();
        opts[clients.size()] = "Annuler";

        int choix = cli.selectionner("Choisir le client", opts);
        if (choix == clients.size()) return;

        Client client = clients.get(choix);
        afficherCommandes(service.getCommandesParClient(client), "Commandes de " + client.getNom() + " " + client.getPrenom());
    }

    static void afficherCommandes(List<Commande> commandes, String titre) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
        if (commandes.isEmpty()) { cli.afficherInfo("Aucune commande."); }
        else {
            System.out.printf(MenuCLI.CYAN + "  %-5s %-20s %-20s %-12s %-15s%n" + MenuCLI.RESET, "ID", "Client", "Description", "Date", "Statut");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(72) + MenuCLI.RESET);
            for (Commande c : commandes)
                System.out.printf("  %-5d %-20s %-20s %-12s %-15s%n", c.getId(),
                        c.getClient().getNom() + " " + c.getClient().getPrenom(),
                        c.getDescription(), c.getDateCommande(), c.getStatut().getLabel());
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
                    "Affecter une livraison", "Terminer une livraison",
                    "Livraisons en cours", "Historique (livraisons terminees)", "Retour"
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

        List<Commande> affectables = new ArrayList<>();
        for (Commande c : service.getListeCommandes()) {
            if ((c.getStatut() == StatutCommande.EN_ATTENTE || c.getStatut() == StatutCommande.EN_PREPARATION)
                && !service.commandeDejaAffectee(c.getId())) {
                affectables.add(c);
            }
        }
        if (affectables.isEmpty()) { cli.afficherErreur("Aucune commande disponible."); cli.pause(); return; }
        if (service.getListeLivreurs().isEmpty()) { cli.afficherErreur("Aucun livreur."); cli.pause(); return; }

        String[] optsCmd = new String[affectables.size() + 1];
        for (int i = 0; i < affectables.size(); i++) optsCmd[i] = affectables.get(i).toString();
        optsCmd[affectables.size()] = "Annuler";
        int choixCmd = cli.selectionner("Choisir la commande", optsCmd);
        if (choixCmd == affectables.size()) return;

        List<Livreur> livreurs = service.getListeLivreurs();
        String[] optsLiv = new String[livreurs.size() + 1];
        for (int i = 0; i < livreurs.size(); i++) optsLiv[i] = livreurs.get(i).toString();
        optsLiv[livreurs.size()] = "Annuler";
        int choixLiv = cli.selectionner("Choisir le livreur", optsLiv);
        if (choixLiv == livreurs.size()) return;

        int choixType = cli.selectionner("Type de livraison", new String[]{"Standard", "Express"});
        TypeLivraison type = (choixType == 0) ? TypeLivraison.STANDARD : TypeLivraison.EXPRESS;

        cli.clearScreen();
        cli.afficherTitre("Date de livraison prevue");
        cli.afficherInfo("Format: AAAA-MM-JJ (ex: 2026-04-15). Vide = demain");
        String dateStr = cli.lireTexte("Date prevue");

        LocalDate datePrevue;
        if (dateStr.isEmpty()) {
            datePrevue = LocalDate.now().plusDays(1);
        } else {
            try { datePrevue = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE); }
            catch (DateTimeParseException e) { cli.afficherErreur("Format invalide. Demain par defaut."); datePrevue = LocalDate.now().plusDays(1); }
        }

        Livraison liv = new Livraison(affectables.get(choixCmd), livreurs.get(choixLiv), datePrevue, type);
        service.affecterLivraison(liv);
        cli.afficherSucces("Livraison #" + liv.getId() + " affectee (" + type.getLabel() + ")");
        cli.pause();
    }

    static void terminerLivraison() throws IOException {
        cli.clearScreen();
        cli.afficherTitre("Terminer une Livraison");
        List<Livraison> enCours = service.getLivraisonsEnCours();
        if (enCours.isEmpty()) { cli.afficherErreur("Aucune livraison en cours."); cli.pause(); return; }

        String[] opts = new String[enCours.size() + 1];
        for (int i = 0; i < enCours.size(); i++) opts[i] = enCours.get(i).toString();
        opts[enCours.size()] = "Annuler";

        int choix = cli.selectionner("Choisir la livraison a terminer", opts);
        if (choix == enCours.size()) return;

        Livraison liv = enCours.get(choix);
        liv.terminerLivraison();
        cli.afficherSucces("Livraison #" + liv.getId() + " terminee - Commande #"
                + liv.getCommande().getId() + " livree a " + liv.getCommande().getClient().getNom());
        cli.pause();
    }

    static void afficherLivraisons(List<Livraison> livraisons, String titre) throws IOException {
        cli.clearScreen();
        cli.afficherTitre(titre);
        if (livraisons.isEmpty()) { cli.afficherInfo("Aucune livraison."); }
        else {
            System.out.printf(MenuCLI.CYAN + "  %-4s %-6s %-15s %-15s %-10s %-12s %-10s%n" + MenuCLI.RESET, "ID", "Cmd", "Client", "Livreur", "Type", "Prevue", "Statut");
            System.out.println(MenuCLI.DIM + "  " + "-".repeat(72) + MenuCLI.RESET);
            for (Livraison l : livraisons) {
                String statut = l.estTerminee() ? "Terminee" : "En cours";
                System.out.printf("  %-4d %-6d %-15s %-15s %-10s %-12s %-10s%n", l.getId(),
                        l.getCommande().getId(), l.getCommande().getClient().getNom(),
                        l.getLivreur().getNom(), l.getType().getLabel(), l.getDateLivraisonPrevue(), statut);
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
            int nb = service.getNombreLivraisonsParLivreur(meilleur);
            System.out.println("  Livreur + actif  :  " + MenuCLI.BOLD + meilleur.getNom() + " " + meilleur.getPrenom() + " (" + nb + " livraison(s))" + MenuCLI.RESET);
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
