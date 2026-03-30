public class Livreur extends Personne {
    private String vehicule;
    private static int compteur = 1;

    public Livreur(String nom, String prenom, String telephone, String vehicule) {
        super(compteur++, nom, prenom, telephone);
        this.vehicule = vehicule;
    }

    @Override
    public void afficherDetails() {
        System.out.println("  +-----------------------------+");
        System.out.printf("  | Livreur #%-18d |%n", getId());
        System.out.printf("  | Nom:       %-17s |%n", getNom() + " " + getPrenom());
        System.out.printf("  | Telephone: %-17s |%n", getTelephone());
        System.out.printf("  | Vehicule:  %-17s |%n", vehicule);
        System.out.println("  +-----------------------------+");
    }

    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }

    @Override
    public String toString() {
        return "#" + getId() + " " + getNom() + " " + getPrenom() + " (" + vehicule + ")";
    }
}
