public class Client extends Personne {
    private String email;
    private String adresse;
    private static int compteur = 1;

    public Client(String nom, String prenom, String telephone, String email, String adresse) {
        super(compteur++, nom, prenom, telephone);
        this.email = email;
        this.adresse = adresse;
    }

    @Override
    public void afficherDetails() {
        System.out.println("  +-----------------------------+");
        System.out.printf("  | Client #%-19d |%n", getId());
        System.out.printf("  | Nom:       %-17s |%n", getNom() + " " + getPrenom());
        System.out.printf("  | Telephone: %-17s |%n", getTelephone());
        System.out.printf("  | Email:     %-17s |%n", email);
        System.out.printf("  | Adresse:   %-17s |%n", adresse);
        System.out.println("  +-----------------------------+");
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}
