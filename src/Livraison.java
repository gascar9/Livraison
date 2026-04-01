import java.time.LocalDate;

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
}
