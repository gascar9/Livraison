import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServiceLivraison {
    private ArrayList<Client> listeClients = new ArrayList<>();
    private ArrayList<Livreur> listeLivreurs = new ArrayList<>();
    private ArrayList<Commande> listeCommandes = new ArrayList<>();
    private ArrayList<Livraison> listeLivraisons = new ArrayList<>();

    // ═══ CLIENTS ═══

    public void ajouterClient(Client client) {
        listeClients.add(client);
    }

    public boolean peutSupprimerClient(int id) {
        for (Commande c : listeCommandes) {
            if (c.getClient().getId() == id && c.getStatut() != StatutCommande.LIVREE) {
                return false;
            }
        }
        return true;
    }

    public boolean supprimerClient(int id) {
        listeCommandes.removeIf(c -> c.getClient().getId() == id);
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

    public List<Client> getListeClients() {
        return Collections.unmodifiableList(listeClients);
    }

    // ═══ LIVREURS ═══

    public void ajouterLivreur(Livreur livreur) {
        listeLivreurs.add(livreur);
    }

    public boolean peutSupprimerLivreur(int id) {
        for (Livraison l : listeLivraisons) {
            if (l.getLivreur().getId() == id && !l.estTerminee()) {
                return false;
            }
        }
        return true;
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

    public ArrayList<Livreur> getLivreursTriesParNom() {
        ArrayList<Livreur> copie = new ArrayList<>(listeLivreurs);
        copie.sort(Comparator.comparing(Livreur::getNom)
                .thenComparing(Livreur::getPrenom));
        return copie;
    }

    public List<Livreur> getListeLivreurs() {
        return Collections.unmodifiableList(listeLivreurs);
    }

    // ═══ COMMANDES ═══

    public void creerCommande(Commande commande) {
        listeCommandes.add(commande);
    }

    public boolean peutSupprimerCommande(int id) {
        for (Livraison l : listeLivraisons) {
            if (l.getCommande().getId() == id && !l.estTerminee()) {
                return false;
            }
        }
        return true;
    }

    public boolean supprimerCommande(int id) {
        listeLivraisons.removeIf(l -> l.getCommande().getId() == id);
        return listeCommandes.removeIf(c -> c.getId() == id);
    }

    public Commande rechercherCommandeParId(int id) {
        for (Commande c : listeCommandes) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public ArrayList<Commande> rechercherCommandeParDescription(String texte) {
        ArrayList<Commande> resultats = new ArrayList<>();
        String texteLower = texte.toLowerCase();
        for (Commande c : listeCommandes) {
            if (c.getDescription().toLowerCase().contains(texteLower)) {
                resultats.add(c);
            }
        }
        return resultats;
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

    public List<Commande> getListeCommandes() {
        return Collections.unmodifiableList(listeCommandes);
    }

    // ═══ LIVRAISONS ═══

    public boolean commandeDejaAffectee(int commandeId) {
        for (Livraison l : listeLivraisons) {
            if (l.getCommande().getId() == commandeId && !l.estTerminee()) {
                return true;
            }
        }
        return false;
    }

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

    public List<Livraison> getListeLivraisons() {
        return Collections.unmodifiableList(listeLivraisons);
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
