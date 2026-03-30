import java.util.ArrayList;
import java.util.Comparator;

public class ServiceLivraison {
    private ArrayList<Client> listeClients = new ArrayList<>();
    private ArrayList<Livreur> listeLivreurs = new ArrayList<>();
    private ArrayList<Commande> listeCommandes = new ArrayList<>();
    private ArrayList<Livraison> listeLivraisons = new ArrayList<>();

    // ═══ CLIENTS ═══

    public void ajouterClient(Client client) {
        listeClients.add(client);
    }

    public boolean supprimerClient(int id) {
        return listeClients.removeIf(c -> c.getId() == id);
    }

    public Client rechercherClientParId(int id) {
        for (Client c : listeClients) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public ArrayList<Client> rechercherClientParNom(String nom) {
        ArrayList<Client> resultats = new ArrayList<>();
        String nomLower = nom.toLowerCase();
        for (Client c : listeClients) {
            if (c.getNom().toLowerCase().contains(nomLower)
                || c.getPrenom().toLowerCase().contains(nomLower)) {
                resultats.add(c);
            }
        }
        return resultats;
    }

    public ArrayList<Client> getClientsTriesParNom() {
        ArrayList<Client> copie = new ArrayList<>(listeClients);
        copie.sort(Comparator.comparing(Client::getNom)
                .thenComparing(Client::getPrenom));
        return copie;
    }

    public ArrayList<Client> getListeClients() {
        return listeClients;
    }

    // ═══ LIVREURS ═══

    public void ajouterLivreur(Livreur livreur) {
        listeLivreurs.add(livreur);
    }

    public boolean supprimerLivreur(int id) {
        return listeLivreurs.removeIf(l -> l.getId() == id);
    }

    public Livreur rechercherLivreurParId(int id) {
        for (Livreur l : listeLivreurs) {
            if (l.getId() == id) return l;
        }
        return null;
    }

    public ArrayList<Livreur> rechercherLivreurParNom(String nom) {
        ArrayList<Livreur> resultats = new ArrayList<>();
        String nomLower = nom.toLowerCase();
        for (Livreur l : listeLivreurs) {
            if (l.getNom().toLowerCase().contains(nomLower)
                || l.getPrenom().toLowerCase().contains(nomLower)) {
                resultats.add(l);
            }
        }
        return resultats;
    }

    public ArrayList<Livreur> getListeLivreurs() {
        return listeLivreurs;
    }

    // ═══ COMMANDES ═══

    public void creerCommande(Commande commande) {
        listeCommandes.add(commande);
    }

    public boolean supprimerCommande(int id) {
        return listeCommandes.removeIf(c -> c.getId() == id);
    }

    public Commande rechercherCommandeParId(int id) {
        for (Commande c : listeCommandes) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public ArrayList<Commande> getCommandesTrieesParDate() {
        ArrayList<Commande> copie = new ArrayList<>(listeCommandes);
        copie.sort(Comparator.comparing(Commande::getDateCommande).reversed());
        return copie;
    }

    public ArrayList<Commande> getCommandesParClient(Client client) {
        ArrayList<Commande> resultats = new ArrayList<>();
        for (Commande c : listeCommandes) {
            if (c.getClient().getId() == client.getId()) {
                resultats.add(c);
            }
        }
        return resultats;
    }

    public ArrayList<Commande> getCommandesEnLivraison() {
        ArrayList<Commande> resultats = new ArrayList<>();
        for (Commande c : listeCommandes) {
            if (c.getStatut() == StatutCommande.EN_LIVRAISON) {
                resultats.add(c);
            }
        }
        return resultats;
    }

    public ArrayList<Commande> getListeCommandes() {
        return listeCommandes;
    }

    // ═══ LIVRAISONS ═══

    public void affecterLivraison(Livraison livraison) {
        listeLivraisons.add(livraison);
    }

    public ArrayList<Livraison> getLivraisonsEnCours() {
        ArrayList<Livraison> resultats = new ArrayList<>();
        for (Livraison l : listeLivraisons) {
            if (!l.estTerminee()) {
                resultats.add(l);
            }
        }
        return resultats;
    }

    public ArrayList<Livraison> getLivraisonsTerminees() {
        ArrayList<Livraison> resultats = new ArrayList<>();
        for (Livraison l : listeLivraisons) {
            if (l.estTerminee()) {
                resultats.add(l);
            }
        }
        return resultats;
    }

    public ArrayList<Livraison> getListeLivraisons() {
        return listeLivraisons;
    }

    // ═══ STATISTIQUES ═══

    public int getNombreCommandesLivrees() {
        int count = 0;
        for (Commande c : listeCommandes) {
            if (c.getStatut() == StatutCommande.LIVREE) count++;
        }
        return count;
    }

    public int getNombreLivraisonsParLivreur(Livreur livreur) {
        int count = 0;
        for (Livraison l : listeLivraisons) {
            if (l.getLivreur().getId() == livreur.getId() && l.estTerminee()) {
                count++;
            }
        }
        return count;
    }

    public Livreur getLivreurPlusActif() {
        Livreur meilleur = null;
        int max = 0;
        for (Livreur l : listeLivreurs) {
            int nb = getNombreLivraisonsParLivreur(l);
            if (nb > max) {
                max = nb;
                meilleur = l;
            }
        }
        return meilleur;
    }
}
