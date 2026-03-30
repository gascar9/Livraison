import java.time.LocalDate;

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
}
