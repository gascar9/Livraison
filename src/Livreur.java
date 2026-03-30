public class Livreur {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;
    private static int compteur = 1;

    public Livreur(String nom, String prenom, String telephone, String vehicule) {
        this.id = compteur++;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.vehicule = vehicule;
    }

    public void afficherDetails() {
        System.out.println("  +-----------------------------+");
        System.out.printf("  | Livreur #%-18d |%n", id);
        System.out.printf("  | Nom:       %-17s |%n", nom + " " + prenom);
        System.out.printf("  | Telephone: %-17s |%n", telephone);
        System.out.printf("  | Vehicule:  %-17s |%n", vehicule);
        System.out.println("  +-----------------------------+");
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }

    @Override
    public String toString() {
        return "#" + id + " " + nom + " " + prenom + " (" + vehicule + ")";
    }
}
